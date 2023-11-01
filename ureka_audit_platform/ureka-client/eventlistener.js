const Web3 = require('web3');
const web3 = new Web3('ws://127.0.0.1:7545');
const fs = require("fs");
const contract_data = JSON.parse(
    fs.readFileSync('C:/Users/Jerry/Documents/UREKA/build/contracts/UREKA_sale.json')
    );
var contract_addr = process.argv[2];
const abiDecoder = require('abi-decoder'); 
abiDecoder.addABI(contract_data.abi);
//console.log(typeof(contract_addr));

var subscription = web3.eth.subscribe('logs', {address: contract_addr},
    (error, result) => {if (error) console.error(error);})
    .on("connected", function (subscriptionId) {
        console.log("Subscription ID is:" + subscriptionId);
    })
    .on("data", function (log) {       
            const decodedLogs = abiDecoder.decodeLogs([log]);
            console.log(decodedLogs[0]);
            //web3.eth.getAccounts(console.log);
    });          

process.on('SIGINT', function () {
    subscription.unsubscribe(function(error, success){
        if(success)
            console.log('Unsubscribed.');
            process.exit();
    });  
});
