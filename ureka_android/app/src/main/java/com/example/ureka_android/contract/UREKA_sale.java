package com.example.ureka_android.contract;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple10;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class UREKA_sale extends Contract {
    public static final String BINARY = "0x6080604052604051620013c6380380620013c6833981810160405260408110156200002957600080fd5b81019080805160405193929190846401000000008211156200004a57600080fd5b838201915060208201858111156200006157600080fd5b82518660018202830111640100000000821117156200007f57600080fd5b8083526020830192505050908051906020019080838360005b83811015620000b557808201518184015260208101905062000098565b50505050905090810190601f168015620000e35780820380516001836020036101000a031916815260200191505b50604052602001805160405193929190846401000000008211156200010757600080fd5b838201915060208201858111156200011e57600080fd5b82518660018202830111640100000000821117156200013c57600080fd5b8083526020830192505050908051906020019080838360005b838110156200017257808201518184015260208101905062000155565b50505050905090810190601f168015620001a05780820380516001836020036101000a031916815260200191505b5060405250505033600660006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555034600181905550600154600381905550600154600354016004819055506000600760146101000a81548160ff0219169083151502179055506000600760156101000a81548160ff02191690831515021790555043600b8190555042600c819055508160009080519060200190620002619291906200028b565b5080600a90805190602001906200027a92919062000312565b5060006005819055505050620003c1565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10620002ce57805160ff1916838001178555620002ff565b82800160010185558215620002ff579182015b82811115620002fe578251825591602001919060010190620002e1565b5b5090506200030e919062000399565b5090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200035557805160ff191683800117855562000386565b8280016001018555821562000386579182015b828111156200038557825182559160200191906001019062000368565b5b50905062000395919062000399565b5090565b620003be91905b80821115620003ba576000816000905550600101620003a0565b5090565b90565b610ff580620003d16000396000f3fe60806040526004361061004a5760003560e01c8063376425ca1461004f5780634baf8110146101175780634d1e1e661461012e5780635e94538d14610327578063ad5c613d146103ef575b600080fd5b34801561005b57600080fd5b506101156004803603602081101561007257600080fd5b810190808035906020019064010000000081111561008f57600080fd5b8201836020820111156100a157600080fd5b803590602001918460018302840111640100000000831117156100c357600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f8201169050808301925050505050505091929192905050506104aa565b005b34801561012357600080fd5b5061012c610614565b005b34801561013a57600080fd5b50610143610710565b60405180806020018b81526020018a81526020018973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018715151515815260200186151515158152602001806020018060200185815260200184810384528e818151815260200191508051906020019080838360005b838110156102155780820151818401526020810190506101fa565b50505050905090810190601f1680156102425780820380516001836020036101000a031916815260200191505b50848103835287818151815260200191508051906020019080838360005b8381101561027b578082015181840152602081019050610260565b50505050905090810190601f1680156102a85780820380516001836020036101000a031916815260200191505b50848103825286818151815260200191508051906020019080838360005b838110156102e15780820151818401526020810190506102c6565b50505050905090810190601f16801561030e5780820380516001836020036101000a031916815260200191505b509d505050505050505050505050505060405180910390f35b34801561033357600080fd5b506103ed6004803603602081101561034a57600080fd5b810190808035906020019064010000000081111561036757600080fd5b82018360208201111561037957600080fd5b8035906020019184600183028401116401000000008311171561039b57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f82011690508083019250505050505050919291929050505061098e565b005b6104a86004803603602081101561040557600080fd5b810190808035906020019064010000000081111561042257600080fd5b82018360208201111561043457600080fd5b8035906020019184600183028401116401000000008311171561045657600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290505050610b1b565b005b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161461056d576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260178152602001807f596f7520617265206e6f74207468652073656c6c65722e00000000000000000081525060200191505060405180910390fd5b7f0811097a47a143ac52e01896e7e5a10f233ab95b95dea26cd790844c6eaf04ef42826040518083815260200180602001828103825283818151815260200191508051906020019080838360005b838110156105d65780820151818401526020810190506105bb565b50505050905090810190601f1680156106035780820380516001836020036101000a031916815260200191505b50935050505060405180910390a150565b600760009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146106d7576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260168152602001807f596f7520617265206e6f74207468652062757965722e0000000000000000000081525060200191505060405180910390fd5b7f35269ce44cd7fa4e9c4e0a61d9f6d94af5bb0e81cdd89697d6a0631577f6b6b9426040518082815260200191505060405180910390a1565b6060600080600080600080606080600080600454600354600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16600760009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16600760149054906101000a900460ff16600760159054906101000a900460ff16600a6009600554898054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156108295780601f106107fe57610100808354040283529160200191610829565b820191906000526020600020905b81548152906001019060200180831161080c57829003601f168201915b50505050509950869650859550828054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156108cb5780601f106108a0576101008083540402835291602001916108cb565b820191906000526020600020905b8154815290600101906020018083116108ae57829003601f168201915b50505050509250818054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156109675780601f1061093c57610100808354040283529160200191610967565b820191906000526020600020905b81548152906001019060200180831161094a57829003601f168201915b50505050509150995099509950995099509950995099509950995090919293949596979899565b600760009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610a51576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260168152602001807f596f7520617265206e6f74207468652062757965722e0000000000000000000081525060200191505060405180910390fd5b6000600760146101000a81548160ff0219169083151502179055507f12e8dbb6c0daae71d0cc5b390fda89946cdb36841e601af7b2218a72f8f483dd42826040518083815260200180602001828103825283818151815260200191508051906020019080838360005b83811015610ad5578082015181840152602081019050610aba565b50505050905090810190601f168015610b025780820380516001836020036101000a031916815260200191505b50935050505060405180910390a1610b18610e24565b50565b60001515600760149054906101000a900460ff16151514610ba4576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260188152602001807f54686973206974656d20686173206265656e20736f6c642e000000000000000081525060200191505060405180910390fd5b6004543414610c1b576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601b8152602001807f57726f6e6720707572636861736520616d6f756e742073656e742e000000000081525060200191505060405180910390fd5b33600760006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508060099080519060200190610c72929190610f1b565b506001546002819055507f2b7337d8887d7e02b0d00cd2e610db65d2425558794b103be7c3b733df656c59600760009054906101000a900473ffffffffffffffffffffffffffffffffffffffff166009600454600a604051808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018060200184815260200180602001838103835286818154600181600116156101000203166002900481526020019150805460018160011615610100020316600290048015610d8c5780601f10610d6157610100808354040283529160200191610d8c565b820191906000526020600020905b815481529060010190602001808311610d6f57829003601f168201915b5050838103825284818154600181600116156101000203166002900481526020019150805460018160011615610100020316600290048015610e0f5780601f10610de457610100808354040283529160200191610e0f565b820191906000526020600020905b815481529060010190602001808311610df257829003601f168201915b5050965050505050505060405180910390a150565b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc600354600154019081150290604051600060405180830381858888f19350505050158015610e92573d6000803e3d6000fd5b50600760009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc6002549081150290604051600060405180830381858888f19350505050158015610efd573d6000803e3d6000fd5b506001600760156101000a81548160ff021916908315150217905550565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610f5c57805160ff1916838001178555610f8a565b82800160010185558215610f8a579182015b82811115610f89578251825591602001919060010190610f6e565b5b509050610f979190610f9b565b5090565b610fbd91905b80821115610fb9576000816000905550600101610fa1565b5090565b9056fea265627a7a72315820bbab332c3dd7dc536a78f5b6484a9a64cca2662f891ef8287026168e293fc72064736f6c63430005100032";

    public static final String FUNC_PURCHASE = "purchase";

    public static final String FUNC_UPLOAD_UTICKET = "upload_uticket";

    public static final String FUNC_RETRIVE_UTICKET = "retrive_uticket";

    public static final String FUNC_UPLOAD_RTICKET = "upload_rticket";

    public static final String FUNC_READ_LISTING = "read_listing";

    public static final Event ITEM_SOLD_EVENT = new Event("item_sold",
            Arrays.asList(new TypeReference<Address>() {
            }, new TypeReference<DynamicBytes>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<DynamicBytes>() {
            }));

    public static final Event RTICKET_UPLOADED_EVENT = new Event("rTicket_uploaded",
            Arrays.asList(new TypeReference<Uint256>() {
            }, new TypeReference<Utf8String>() {
            }));

    public static final Event UTICKET_RETRIVED_EVENT = new Event("uTicket_retrived",
            Arrays.asList(new TypeReference<Uint256>() {
            }));

    public static final Event UTICKET_UPLOADED_EVENT = new Event("uTicket_uploaded",
            Arrays.asList(new TypeReference<Uint256>() {
            }, new TypeReference<Utf8String>() {
            }));

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("5777", "0x21fB3e0d2cD81d91277dDC6B7305fd75caa4CA7A");
    }

    @Deprecated
    protected UREKA_sale(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected UREKA_sale(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected UREKA_sale(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected UREKA_sale(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<Item_soldEventResponse> getItem_soldEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ITEM_SOLD_EVENT, transactionReceipt);
        ArrayList<Item_soldEventResponse> responses = new ArrayList<Item_soldEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            Item_soldEventResponse typedResponse = new Item_soldEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sold_to = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.sold_to_id = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.sold_price = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.device_id = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<Item_soldEventResponse> item_soldEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, Item_soldEventResponse>() {
            @Override
            public Item_soldEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ITEM_SOLD_EVENT, log);
                Item_soldEventResponse typedResponse = new Item_soldEventResponse();
                typedResponse.log = log;
                typedResponse.sold_to = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.sold_to_id = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.sold_price = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.device_id = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<Item_soldEventResponse> item_soldEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ITEM_SOLD_EVENT));
        return item_soldEventFlowable(filter);
    }

    public List<RTicket_uploadedEventResponse> getRTicket_uploadedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(RTICKET_UPLOADED_EVENT, transactionReceipt);
        ArrayList<RTicket_uploadedEventResponse> responses = new ArrayList<RTicket_uploadedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RTicket_uploadedEventResponse typedResponse = new RTicket_uploadedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.upload_time = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.rTicket = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RTicket_uploadedEventResponse> rTicket_uploadedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RTicket_uploadedEventResponse>() {
            @Override
            public RTicket_uploadedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(RTICKET_UPLOADED_EVENT, log);
                RTicket_uploadedEventResponse typedResponse = new RTicket_uploadedEventResponse();
                typedResponse.log = log;
                typedResponse.upload_time = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.rTicket = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RTicket_uploadedEventResponse> rTicket_uploadedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RTICKET_UPLOADED_EVENT));
        return rTicket_uploadedEventFlowable(filter);
    }

    public List<UTicket_retrivedEventResponse> getUTicket_retrivedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(UTICKET_RETRIVED_EVENT, transactionReceipt);
        ArrayList<UTicket_retrivedEventResponse> responses = new ArrayList<UTicket_retrivedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UTicket_retrivedEventResponse typedResponse = new UTicket_retrivedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.retrive_time = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<UTicket_retrivedEventResponse> uTicket_retrivedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, UTicket_retrivedEventResponse>() {
            @Override
            public UTicket_retrivedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(UTICKET_RETRIVED_EVENT, log);
                UTicket_retrivedEventResponse typedResponse = new UTicket_retrivedEventResponse();
                typedResponse.log = log;
                typedResponse.retrive_time = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<UTicket_retrivedEventResponse> uTicket_retrivedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UTICKET_RETRIVED_EVENT));
        return uTicket_retrivedEventFlowable(filter);
    }

    public List<UTicket_uploadedEventResponse> getUTicket_uploadedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(UTICKET_UPLOADED_EVENT, transactionReceipt);
        ArrayList<UTicket_uploadedEventResponse> responses = new ArrayList<UTicket_uploadedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UTicket_uploadedEventResponse typedResponse = new UTicket_uploadedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.upload_time = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.uTicket = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<UTicket_uploadedEventResponse> uTicket_uploadedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, UTicket_uploadedEventResponse>() {
            @Override
            public UTicket_uploadedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(UTICKET_UPLOADED_EVENT, log);
                UTicket_uploadedEventResponse typedResponse = new UTicket_uploadedEventResponse();
                typedResponse.log = log;
                typedResponse.upload_time = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.uTicket = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<UTicket_uploadedEventResponse> uTicket_uploadedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UTICKET_UPLOADED_EVENT));
        return uTicket_uploadedEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> purchase(byte[] _user_id, BigInteger weiValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PURCHASE,
                Arrays.asList(new org.web3j.abi.datatypes.DynamicBytes(_user_id)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<TransactionReceipt> upload_uticket(String ticket) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UPLOAD_UTICKET,
                Arrays.asList(new org.web3j.abi.datatypes.Utf8String(ticket)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> retrive_uticket() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RETRIVE_UTICKET,
                Arrays.asList(),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> upload_rticket(String ticket) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UPLOAD_RTICKET,
                Arrays.asList(new org.web3j.abi.datatypes.Utf8String(ticket)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple10<String, BigInteger, BigInteger, String, String, Boolean, Boolean, byte[], byte[], BigInteger>> read_listing() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_READ_LISTING,
                Arrays.asList(),
                Arrays.asList(new TypeReference<Utf8String>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Address>() {
                }, new TypeReference<Address>() {
                }, new TypeReference<Bool>() {
                }, new TypeReference<Bool>() {
                }, new TypeReference<DynamicBytes>() {
                }, new TypeReference<DynamicBytes>() {
                }, new TypeReference<Uint256>() {
                }));
        return new RemoteFunctionCall<Tuple10<String, BigInteger, BigInteger, String, String, Boolean, Boolean, byte[], byte[], BigInteger>>(function,
                new Callable<Tuple10<String, BigInteger, BigInteger, String, String, Boolean, Boolean, byte[], byte[], BigInteger>>() {
                    @Override
                    public Tuple10<String, BigInteger, BigInteger, String, String, Boolean, Boolean, byte[], byte[], BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple10<String, BigInteger, BigInteger, String, String, Boolean, Boolean, byte[], byte[], BigInteger>(
                                (String) results.get(0).getValue(),
                                (BigInteger) results.get(1).getValue(),
                                (BigInteger) results.get(2).getValue(),
                                (String) results.get(3).getValue(),
                                (String) results.get(4).getValue(),
                                (Boolean) results.get(5).getValue(),
                                (Boolean) results.get(6).getValue(),
                                (byte[]) results.get(7).getValue(),
                                (byte[]) results.get(8).getValue(),
                                (BigInteger) results.get(9).getValue());
                    }
                });
    }

    @Deprecated
    public static UREKA_sale load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new UREKA_sale(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static UREKA_sale load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new UREKA_sale(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static UREKA_sale load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new UREKA_sale(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static UREKA_sale load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new UREKA_sale(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<UREKA_sale> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, BigInteger initialWeiValue, String _name, byte[] _device_id) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.asList(new org.web3j.abi.datatypes.Utf8String(_name),
                new org.web3j.abi.datatypes.DynamicBytes(_device_id)));
        return deployRemoteCall(UREKA_sale.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor, initialWeiValue);
    }

    public static RemoteCall<UREKA_sale> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, BigInteger initialWeiValue, String _name, byte[] _device_id) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.asList(new org.web3j.abi.datatypes.Utf8String(_name),
                new org.web3j.abi.datatypes.DynamicBytes(_device_id)));
        return deployRemoteCall(UREKA_sale.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor, initialWeiValue);
    }

    @Deprecated
    public static RemoteCall<UREKA_sale> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue, String _name, byte[] _device_id) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.asList(new org.web3j.abi.datatypes.Utf8String(_name),
                new org.web3j.abi.datatypes.DynamicBytes(_device_id)));
        return deployRemoteCall(UREKA_sale.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor, initialWeiValue);
    }

    @Deprecated
    public static RemoteCall<UREKA_sale> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue, String _name, byte[] _device_id) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.asList(new org.web3j.abi.datatypes.Utf8String(_name),
                new org.web3j.abi.datatypes.DynamicBytes(_device_id)));
        return deployRemoteCall(UREKA_sale.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor, initialWeiValue);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class Item_soldEventResponse extends BaseEventResponse {
        public String sold_to;

        public byte[] sold_to_id;

        public BigInteger sold_price;

        public byte[] device_id;
    }

    public static class RTicket_uploadedEventResponse extends BaseEventResponse {
        public BigInteger upload_time;

        public String rTicket;
    }

    public static class UTicket_retrivedEventResponse extends BaseEventResponse {
        public BigInteger retrive_time;
    }

    public static class UTicket_uploadedEventResponse extends BaseEventResponse {
        public BigInteger upload_time;

        public String uTicket;
    }
}
