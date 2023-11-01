const Web3 = require('web3');
const newProvider = () => new Web3.providers.WebsocketProvider('ws://localhost:7545', {
    reconnect: {
        auto: true,
        delay: 5000, // ms
        maxAttempts: 5,
        onTimeout: true,
    },
});
const web3 = new Web3(newProvider());
const provider = web3.currentProvider;

const fs = require("fs");
const contract_data = JSON.parse(
    fs.readFileSync('C:/Users/Jerry/Documents/UREKA/build/contracts/UREKA_sale.json')
);
//console.log(contract_data);
var ureka_contract = new web3.eth.Contract(contract_data.abi);
const readLineSync = require('readline-sync');
menu();

async function menu(){
    console.log("1. Read existing listing");
    console.log("2. Purchase from listing");
    console.log("3. Retrieve uTicket");
    console.log("4. Upload rTicket");
    console.log("0. Exit");
    var userRes = readLineSync.question("Select function: ");
    if (userRes === '1') {
        console.log("Read");
        ureka_contract.options.address = readLineSync.question("Contract Address: ");
        let info = await ureka_contract.methods.read_listing().call();
        console.log("\nName: "+ info._name);
        console.log("Price: "+ web3.utils.fromWei(info._item_price) + " ETH");
        console.log("Seller: "+ info._Seller);
        console.log("Buyer: "+ info._Buyer);
        console.log("Is sold:"+ info._sold);
        console.log("Sale closed:"+ info._sale_closed+"\n");

    } else if (userRes === '2') {
        console.log("Purchase");
        let acct = await get_active_acct();
        ureka_contract.options.address = readLineSync.question("Contract Address: ");
        let info = await ureka_contract.methods.read_listing().call();
        console.log("\nName: "+ info._name);
        console.log("Price: "+ web3.utils.fromWei(info._item_price) + " ETH");
        console.log("Seller: "+ info._Seller);
        console.log("Buyer: "+ info._Buyer);
        console.log("Is sold:"+ info._sold);
        console.log("Sale closed:"+ info._sale_closed+"\n");    
        let price = parseInt(info._item_price)*2;
        //console.log(price);
        price = price.toString();
        //console.log(price);
        let confirm = readLineSync.question("You are about to spend" + price + ", Confirm purchase? ");
        if (confirm === 'y'){
            await ureka_contract.methods.purchase().send({
                from: acct,
                gas: 1500000,
                gasPrice: '80000000',
                value: price
            }, function (error, transactionHash) {    
            }).on('error', function (error) {
                console.log('error', error);
            }).on('transactionHash', function (transactionHash) {
                console.log('transactionHash', transactionHash);
            }).on('receipt', function (receipt) {
                console.log('receipt', receipt.contractAddress);
            }).on('confirmation', function (confirmationNumber, receipt) {
                console.log('confirmation', confirmationNumber);
            });
        }
        else {
            console.log("Purchase cancelled.");
        };

    } else if (userRes === '3') {
        console.log("Retrieve");
        ureka_contract.options.address = readLineSync.question("Contract Address: ");
        await ureka_contract.getPastEvents("uTicket_uploaded",{fromBlock: 1},(error, events) => {
            if (error) console.error(error);
            else {
                let uTicket = events.uTicket;
                fs.writeFileSync('C:/Users/Jerry/Documents/UREKA/ureka-client/dummy_u_retrived.tk', uTicket, 'binary');
            }        
        });

    } else if (userRes === '4') {
        console.log("Upload");
        let acct = await get_active_acct();
        ureka_contract.options.address = readLineSync.question("Contract Address: ");
        let rTicket = fs.readFileSync('C:/Users/Jerry/Documents/UREKA/ureka-client/dummy_r_raw.tk', 'binary');
        await ureka_contract.methods.upload_rticket(rTicket).send({
            from: acct,
            gas: 1500000,
            gasPrice: '80000000',
        }, function (error, transactionHash) {    
        }).on('error', function (error) {
            console.log('error', error);
        }).on('transactionHash', function (transactionHash) {
            console.log('transactionHash', transactionHash);
        }).on('receipt', function (receipt) {
            console.log('receipt', receipt.contractAddress);
        }).on('confirmation', function (confirmationNumber, receipt) {
            console.log('confirmation', confirmationNumber);
        });
        
    } else if (userRes === '0') {
        process.exit(0);
    } else {
        console.log('Please select a valid option');
    }
    menu();
}

async function get_active_acct(){
    var account = await web3.eth.getAccounts();
    account.forEach((item, index) => {
        console.log('[' + index + '] ' + item);
    });
    var choice = readLineSync.question("Choose account: ");
    var active_acct = account[parseInt(choice)];
    console.log(active_acct);
    return active_acct;
}

//provider.on('connect', () => console.log('Connected to blockchain!'));
provider.on('error', (err) => console.log('WEB3_ERROR', err.message));
provider.on('end', (err) => console.error('WEB3_END', err));
