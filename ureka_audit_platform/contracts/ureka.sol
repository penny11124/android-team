//SPDX-License-Identifier: UNLICENSED
//0.5.8 used while developing
pragma solidity >=0.4.21 <0.6.0;

contract UREKA_sale {
    string name;
    uint256 SellerDeposit;
    uint256 BuyerDeposit;
    uint256 item_price;
    uint256 BuyerAMT;
    uint256 session;

    address payable Seller;
    address payable Buyer;

    bool sold;
    bool sale_closed;

    bytes owner_id;
    bytes user_id;
    bytes device_id;

    //仲裁用時間紀錄
    uint256 created_block;
    uint256 created_time;

    event item_sold(
        address sold_to,
        bytes sold_to_id,
        uint256 sold_price,
        bytes device_id
    );
    event uTicket_uploaded(uint256 upload_time, string uTicket);
    event uTicket_retrived(uint256 retrive_time);
    event rTicket_uploaded(uint256 upload_time, string rTicket);

    constructor(string memory _name, bytes memory _device_id) public payable {
        //TODO:constructor帶owner_id, bytes_id進來，記得順便改read_listing()

        Seller = msg.sender;
        SellerDeposit = msg.value;
        item_price = SellerDeposit;
        BuyerAMT = item_price + SellerDeposit;
        sold = false;
        sale_closed = false;
        created_block = block.number;
        created_time = block.timestamp; //epoch , good "enough"
        name = _name;
        device_id = _device_id;
        session = 0;
    }

    function purchase(bytes memory _user_id) public payable {
        //check if buying conditions are made
        require(sold == false, "This item has been sold.");
        require(msg.value == BuyerAMT, "Wrong purchase amount sent.");

        //Buying
        Buyer = msg.sender;
        user_id = _user_id;
        BuyerDeposit = SellerDeposit;
        // sold = true;

        emit item_sold(Buyer, user_id, BuyerAMT, device_id); //let oracle catch this event and prompt seller to upload ticket
    }

    function upload_uticket(string memory ticket) public {
        //要把票當參數直接叫進來?
        //FIDO直接對user發票?
        require(msg.sender == Seller, "You are not the seller.");
        // require(sold, "Item not sold yet.");

        // sold = true;
        //TODO:process uTicket

        //prompt buyer to retrive uTicket, put actual ticket in event to save storage
        //compromise to NOT store uticket in contract root
        emit uTicket_uploaded(block.timestamp, ticket);
    }

    function retrive_uticket() public {
        require(msg.sender == Buyer, "You are not the buyer.");
        emit uTicket_retrived(block.timestamp);
    }

    function upload_rticket(string memory ticket) public {
        require(msg.sender == Buyer, "You are not the buyer.");

        sold = false;
        //validate rTicket

        emit rTicket_uploaded(block.timestamp, ticket); //only for debugging
        close_sale();
    }

    function close_sale() private {
        //TODO: use better transfer pattern

        Seller.transfer(SellerDeposit + item_price);
        Buyer.transfer(BuyerDeposit);

        sale_closed = true;
    }

    function read_listing()
        public
        view
        returns (
            string memory _name,
            uint256 _BuyerAMT,
            uint256 _item_price,
            address _Seller,
            address _Buyer,
            bool _sold,
            bool _sale_closed,
            bytes memory _device_id,
            bytes memory _user_id,
            uint256 _session
        )
    {
        return (
            name,
            BuyerAMT,
            item_price,
            Seller,
            Buyer,
            sold,
            sale_closed,
            device_id,
            user_id,
            session
        );
    }

    //仲裁
    //Timeout
    //add default receive and fallback behavior
}
