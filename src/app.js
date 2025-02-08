const {port, app} = require('./index.js');

module.exports = app.listen(process.env.PORT, () => {
    console.log( process.env.PORT);
    console.log("Listening on http://localhost:" + 3000);
});