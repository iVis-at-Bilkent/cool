const express = require('express');
const app = express();
const cytoscape = require('cytoscape');
const cors = require('cors');

const port = 3000;

// to serve the html
const path = require("path");

// to support sbgnml type of input
let convert = require('sbgnml-to-cytoscape');

// for graphml
const jsdom = require("jsdom");
const { JSDOM } = jsdom;
const { window } = new JSDOM();
const { document } = (new JSDOM('')).window;
global.document = document;

const $ = jQuery = require('jquery')(window);

const graphml = require('cytoscape-graphml');
cytoscape.use(graphml, $);

// for fcose
const fcose = require('cytoscape-fcose');
cytoscape.use(fcose);

// for cose-bilkent
const coseBilkent = require('cytoscape-cose-bilkent');
cytoscape.use(coseBilkent);

// for cise layout, Needs to be fixed, some problems
const cise = require('cytoscape-cise');
cytoscape.use(cise);

// for dagre layout
const dagre = require('cytoscape-dagre');
cytoscape.use(dagre);

// for klay layout
const klay = require('cytoscape-klay');
cytoscape.use(klay);

// TODO test layouts below
// for avsdf layout
const avsdf = require('cytoscape-avsdf');
cytoscape.use(avsdf);

// cola layout
const cola = require('cytoscape-cola');
cytoscape.use(cola);

// euler layout
const euler = require('cytoscape-euler');
cytoscape.use(euler);

const { spawn } = require('child_process');



let cy;
let options;
let data;
let body;

app.use(express.static(path.join(__dirname, "../public/")));
app.use(cors());

// middleware to manage the formats of files
app.use((req, res, next) => {
    console.log( body);
    console.log( req.method);

    if (req.method === "POST") {
        body = '';
        options = '';
        data = '';

        req.on('data', chunk => {
            body += chunk;
        })

        req.on('end', () => {
            //console.log( "Body " + body );
            //const javaClasspath = path.join(__dirname);
           // console.log( javaClasspath);
           /*.:guava-33.4.0-jre.jar:org.eclipse.elk.core_0.9.1.jar:org.eclipse.elk.graph_0.9.1.jar:org.eclipse.emf.common-2.40.0.jar:org.eclipse.emf.ecore_2.38.0.v20241018-1213.jar*/
           /*.:guava-33.4.0-jre.jar:
           org.eclipse.elk.core_0.9.1.jar:
           org.eclipse.elk.graph_0.9.1.jar:
           org.eclipse.emf.common-2.40.0.jar:
           org.eclipse.emf.ecore_2.38.0.v20241018-1213.jar*/
 
           
    
            let id = body.indexOf('{');
            options = body.substring(id);
            data = body.substring(0, id);

            let foundSBGN = body.includes("sbgn");
            let foundGML = body.includes("graphml");
            let isJson = !(foundGML || foundSBGN);
            console.log( "isJson " + isJson);

            if (isJson === true) {
                console.log( "isJson trueee");
                try {
                    body = JSON.parse(body);
                } catch (e) {
                    console.log(e);
                }
                data = body[0];
                options = body[1];
            } else {
                return ;
                try {
                    options = JSON.parse(options);
                } catch (e) {
                    console.log(e);
                }
                if (foundSBGN) { // sbgnml
                    data = convert(data);
                }
            }
            next();
        })
    } else
        next();
})
// whether to include edges in the output or not
// POST /layout/:format?edges=true 
// POST /layout/:format?clusters=true
app.post('/layout/:format', (req, res) => {
    options.animate = false;
    let size = 30;
    console.log( "layout");

    cy = cytoscape({
        styleEnabled: true,
        headless: true
    });

    let nodeCount = 0;
    let edgeCount = 0;
    let edges = [];
    let nodeInfo = {};
    let edgeInfo = {};
    let nodeIdInfo = {};
    let positionInfo = {};
    cy.add(data);
    cy.nodes().forEach(node => {
        nodeInfo[node.data().id] = nodeCount;
        nodeIdInfo[ nodeCount ] = node.data().id;
        nodeCount++;
        console.log( "Node id "  + node.data().id);
    });
    cy.edges().forEach( edge => {
       let a = nodeInfo[edge.data().source];
       let b = nodeInfo[edge.data().target];
       //console.log( edge.data().source + " " + edge.data().target);
       //console.log( a + " " + b);
       edges.push([a,b]);
       //edgeInfo [ a + " " + b ] = edgeCount;
       //edgeCount++;
       console.log( "Edge id "  + edge.data().id);
       edge.remove();

    });
    const javaClasspath = path.join( __dirname, '')
            //console.log( javaClasspath);
            const jarFiles = [
                path.join(__dirname, 'guava-33.4.0-jre.jar'),
                path.join(__dirname, 'org.eclipse.elk.core_0.9.1.jar'),
                path.join(__dirname, 'org.eclipse.elk.graph_0.9.1.jar'),
                path.join(__dirname, 'org.eclipse.emf.common-2.40.0.jar'),
                path.join(__dirname, 'org.eclipse.emf.ecore_2.38.0.v20241018-1213.jar'),
                path.join(__dirname, 'json-20250107.jar')
                // Add more jars if needed
            ].join(':'); // Use `;` for Windows
            // Spawn the Java process
            const classpath = `${__dirname}:${jarFiles}`;
            const javaProcess = spawn('java', ['-cp', classpath, 'PlanarLayoutProvider']);
            
    
            let output = '';
            let error = '';
    
            // Send input to Java program
            javaProcess.stdin.write(nodeCount + '\n' + edges + '\n');
            javaProcess.stdin.end();
            let edgeCountAfterLayout = -1;
            let counterEdge = 0;
            let edgeSource, edgeTarget;
            let numberOfRows = -1;
            let numberOfColumns = -1;
            let row = 0;
            let column = 0;
    
            // Capture Java program's output
            let parserCounter = 0;
            /*javaProcess.stdout.on('data', (data) => {
                console.log( JSON.parse(data));
                if( parserCounter === 1 ){
                    let numberOfRows = 0, numberOfColumns = 0;
                    data = JSON.parse(data);
                    numberOfRows = data.length;
                    numberOfColumns = data[0].length;
                    for( let i = 0; i < numberOfRows; i++){
                        let str = "";
                        for( let j = 0; j < numberOfColumns; j++){
                            str = str + data[i][j] + " ";
                        }
                        console.log( str);
                    }
                }
                parserCounter++;
                
            });
            return ;*/
            
            javaProcess.stdout.on('data', (data) => {
               
               // return ;
               console.log( "Output " + JSON.parse( data)); 
               //data = JSON.parse( data);
               console.log( "Parsed : " + JSON.parse(data).length );
               try {
                console.log("Parsed data : " + JSON.parse(JSON.stringify(data)));
            }
            catch (error) {
                console.log('Error parsing JSON:', error, data);
            }
               
                if( parserCounter === 0){
                    console.log( JSON.parse(data) );                    
                    data = JSON.parse(data);
                    console.log( "Output " + data.length + " " + data[0] + " " + data[1]);
                    for( let i = 0; i < data.length; i++){
                        edgeSource = ( data[i][0] == 'p' || data[i][0] == 'b' || data[i][0] == 'r') ? 
                        parseInt(data[i].substr(2)): parseInt( data[i]);
                        console.log( "Source node " + edgeSource + " "  + nodeIdInfo[ edgeSource] + " " + counterEdge + " " + 2 * edgeCountAfterLayout);
                        if( nodeIdInfo[ edgeSource] === undefined ){
                            console.log( data[i]);
                            nodeIdInfo[ edgeSource] = data[i].substr(0,2) + edgeSource;
                            let dataNode = {
                                id : nodeIdInfo[edgeSource]
                            }
                            var node = cy.add({
                                group: "nodes",
                                data: dataNode
                              });
                            console.log( cy.getElementById( data.id ));
                        }
                        i++;
                        //edgeTarget = parseInt( data[i].substr(2));
                        edgeTarget = ( data[i][0] == 'p' || data[i][0] == 'b' || data[i][0] == 'r') ? 
                        parseInt(data[i].substr(2)): parseInt( data[i]);
                        console.log( "Target node " + edgeTarget + " "  + nodeIdInfo[ edgeTarget]);
                        if( nodeIdInfo[ edgeTarget] === undefined ){
                            nodeIdInfo[ edgeTarget] = data[i].substr(0,2) + edgeTarget;
                            let dataNode = {
                                id : nodeIdInfo[edgeTarget]
                            }
                            var node = cy.add({
                                group: "nodes",
                                data: dataNode
                              });
                        }
                        if( edgeInfo[ edgeSource + " " + edgeTarget] === undefined){
                            edgeInfo[ edgeSource + " " + edgeTarget] = "e" + edgeCount;
                            edgeCount++;
                            let data = {
                                id : edgeInfo[edgeSource + " " + edgeTarget],
                                source : nodeIdInfo[ edgeSource ],
                                target : nodeIdInfo[ edgeTarget ]
                            }
                            var edge = cy.add({
                                group: "edges",
                                data: data
                              });
                        }
                    }
                }
                else if( parserCounter == 3){
                    data  =  JSON.parse(JSON.stringify(data));
                    numberOfRows = data[0];
                    numberOfColumns = data[1];
                    console.log( numberOfRows + " # of cols " + numberOfColumns);
                }
                else if( parserCounter == 1){
                    data = JSON.parse( data);
                    console.log( "Parser 1 step: " + data);
                    numberOfRows = data.length;
                    numberOfColumns = data[0].length;
                    let counter = 0;
                    console.log( "Before positioning the nodes " + numberOfRows + " " + numberOfColumns);
                    for( let i = 0; i < numberOfRows; i++){
                        for( let j = 0; j < numberOfColumns; j++){
                            let value = parseInt( data[i][j]);
                            if( value !== -1){
                            let nodeId = nodeIdInfo[ value ];
                            console.log( "For positioning " + nodeId);
                            positionInfo[ nodeId] = [i, j];
                            }
                            counter++;
                        }
                    }
                }
                parserCounter++;
                /*
                if( edgeCountAfterLayout === -1 ){
                    edgeCountAfterLayout = parseInt(data);
                    console.log( "Edge count " + edgeCountAfterLayout);
                }
                else if ( counterEdge < 2 * edgeCountAfterLayout && counterEdge % 2 == 0){
                    edgeSource = parseInt(data);
                    console.log( "Source node " + edgeSource + " "  + nodeIdInfo[ edgeSource] + " " + counterEdge + " " + 2 * edgeCountAfterLayout);
                    if( nodeIdInfo[ edgeSource] === undefined ){
                        nodeIdInfo[ edgeSource] = "n" + edgeSource;
                        let data = {
                            id : nodeIdInfo[edgeSource]
                        }
                        var node = cy.add({
                            group: "nodes",
                            data: data
                          });
                        console.log( cy.getElementById( data.id ));
                    }
                    counterEdge++;
                    
                }
                else if( counterEdge < 2 * edgeCountAfterLayout && counterEdge % 2 == 1 ){
                    edgeTarget = parseInt( data);
                    console.log( "Target node " + edgeTarget + " "  + nodeIdInfo[ edgeTarget]);
                    if( nodeIdInfo[ edgeTarget] === undefined ){
                        nodeIdInfo[ edgeTarget] = "n" + edgeTarget;
                        let data = {
                            id : nodeIdInfo[edgeTarget]
                        }
                        var node = cy.add({
                            group: "nodes",
                            data: data
                          });
                    }
                    counterEdge++;
                    if( edgeInfo[ edgeSource + " " + edgeTarget] === undefined){
                        edgeInfo[ edgeSource + " " + edgeTarget] = "e" + edgeCount;
                        edgeCount++;
                        let data = {
                            id : edgeInfo[edgeSource + " " + edgeTarget],
                            source : nodeIdInfo[ edgeSource ],
                            target : nodeIdInfo[ edgeTarget ]
                        }
                        var edge = cy.add({
                            group: "edges",
                            data: data
                          });
                    }
                }
                else if( numberOfRows === -1){
                    numberOfRows = parseInt( data );
                }
                else if( numberOfColumns === -1){
                    numberOfColumns = parseInt( data );
                }
                else {
                    if( data !== "null") {
                        let nodeId = nodeIdInfo[ parseInt(data)];
                        console.log( "For positioning " + nodeId);
                        positionInfo[ nodeId] = [row, column];
                    }
                    column++;
                    if( column === numberOfColumns){
                        row++;
                        column = 0;
                    }
                }

                output += data.toString();*/
            });
            javaProcess.stderr.on('data', (data) => {
                error += data.toString();
            });
            cy.nodes().forEach(node => {
                console.log( "Node id "  + node.data().id);
            });
            cy.edges().forEach( edge => {
                console.log( "Edge id " + edge.data().id);
            });
    
            // Handle process close event
            javaProcess.on('close', (code) => {
                if (code === 0) {
                   // resolve(output.trim()); // Resolve with stdout if no errors
                   //console.log( output);
                   cy.filter((element, i) => {
                    return true;
                }).forEach((ele) => {
                    if (ele.isNode()) {
                        if (req.params.format === "json") {
                            let obj = {};
                            console.log( "Ele data before layout : " + ele.data().id);
                            obj["position"] = { x: positionInfo[ele.data().id][0] , y: positionInfo[ele.data().id][1] };
                            obj["data"] = { width: 30, height:30, clusterID: ele.data().clusterID, parent: ele.data().parent, label : (("n") + ele.data().id ) };
                            ret[ele.data().id] = obj;
                        }
                        else if (req.params.format === "graphml") {
                            let obj = {};
                            obj["position"] = { x: parseInt(ele.data('x')), y: parseInt(ele.data('y')) };
                            obj["data"] = { width: parseInt(ele.data('width')), height: parseInt(ele.data('height')), clusterID: parseInt(ele.data('clusterID')), parent: ele.data("parent") };
                            ret[ele.id()] = obj;
                        }
                        else if (req.params.format === "sbgnml") {
                            let obj = {};
                            obj["position"] = { x: ele.data().bbox.x, y: ele.data().bbox.y };
                            obj["data"] = { width: ele.data().bbox.w || ele.data().bbox.width, height: ele.data().bbox.h || ele.data().bbox.height, clusterID: ele.data().clusterID, parent: ele.data().parent };
                            ret[ele.id()] = obj;
                        }
                    }
                    else if (!ele.isNode() && req.query.edges) {
                        ret[ele.id()] = { source: ele.data().source, target: ele.data().target };
                    }
            
                });
            
                return res.status(200).send(ret);
                } else {
                    console.log(`Java process exited with code ${code}. Error: ${error.trim()}`);
                }
            });
    
            // Handle process errors
            javaProcess.on('error', (err) => {
                reject(`Failed to start Java process. Error: ${err.message}`);
            });
            //console.log( output);
    

    //return ;
    console.log( "Request format : " + req.params.format  + " after java process is done ")
    if (req.params.format === "graphml") {
        return ;
        cy.graphml(data);
        cy.filter((element, i) => {
            return element.isNode();
        }).forEach((node) => {
            node.css("width", parseInt(node.data('width')) || size);
            node.css("height", parseInt(node.data('height')) || size);
        })

        cy.layout(options).run();
    }
    else {
        /*cy.add(data);

        cy.filter((element, i) => {
            return element.isNode();
        }).forEach((node) => {
            if (req.params.format === "json") {
                node.css("width", node.data().width || size);
                node.css("height", node.data().height || size);
            }
            else {
                node.css("width", node.data().bbox.w || size);
                node.css("height", node.data().bbox.h || size);
            }
        })*/


        try {
            cy.layout(options).run();
        }
        catch (e) {
            console.log(e);
            return res.status(500).send(e + "");
        }
    }
    let ret = {};

    // whether to return edges or not
    /*cy.filter((element, i) => {
        return true;
    }).forEach((ele) => {
        if (ele.isNode()) {
            if (req.params.format === "json") {
                let obj = {};
                console.log( "Ele data before layout : " + ele.data().id);
                //obj["position"] = { x: positionInfo[ele.data().id][0] * 40, y: positionInfo[ele.data().id][1] * 40};
                obj["data"] = { width: 30, height:30, clusterID: ele.data().clusterID, parent: ele.data().parent };
                ret[ele.data().id] = obj;
            }
            else if (req.params.format === "graphml") {
                let obj = {};
                obj["position"] = { x: parseInt(ele.data('x')), y: parseInt(ele.data('y')) };
                obj["data"] = { width: parseInt(ele.data('width')), height: parseInt(ele.data('height')), clusterID: parseInt(ele.data('clusterID')), parent: ele.data("parent") };
                ret[ele.id()] = obj;
            }
            else if (req.params.format === "sbgnml") {
                let obj = {};
                obj["position"] = { x: ele.data().bbox.x, y: ele.data().bbox.y };
                obj["data"] = { width: ele.data().bbox.w || ele.data().bbox.width, height: ele.data().bbox.h || ele.data().bbox.height, clusterID: ele.data().clusterID, parent: ele.data().parent };
                ret[ele.id()] = obj;
            }
        }
        else if (!ele.isNode() && req.query.edges) {
            ret[ele.id()] = { source: ele.data().source, target: ele.data().target };
        }

    });

    return res.status(200).send(ret);*/
});


module.exports = {
    port,
    app
};