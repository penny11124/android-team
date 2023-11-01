const Ureka = artifacts.require("UREKA_sale");

module.exports = function (deployer, accounts) {
  deployer.deploy(Ureka, {value:"1000000000000000000"});
};
