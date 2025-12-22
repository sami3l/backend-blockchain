package com.clinchain.backend.blockchain;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>
 * Auto generated code.
 * <p>
 * <strong>Do not modify!</strong>
 * <p>
 * Please use the <a href="https://docs.web3j.io/command_line.html">web3j
 * command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/LFDT-web3j/web3j/tree/main/codegen">codegen
 * module</a> to update.
 *
 * <p>
 * Generated with web3j version 1.7.0.
 */
@SuppressWarnings("rawtypes")
public class SupplyChain extends Contract {
    public static final String BINARY = "0x608060405234801561001057600080fd5b506108b5806100206000396000f3fe608060405234801561001057600080fd5b50600436106100565760003560e01c80626e25411461005b578063134ef11f14610070578063a115b2ee14610083578063d3c3f70014610096578063f1648e84146100a9575b600080fd5b61006e610069366004610571565b6100d6565b005b61006e61007e3660046105a0565b6101b7565b61006e610091366004610571565b6102e7565b61006e6100a4366004610571565b6103cb565b6100bc6100b7366004610571565b6104ac565b6040516100cd9594939291906106b7565b60405180910390f35b600081815260208190526040902060028082015460ff1660038111156100fe576100fe6106a1565b146101505760405162461bcd60e51b815260206004820152601d60248201527f4c65206c6f7420646f6974206574726520656e20706861726d6163696500000060448201526064015b60405180910390fd5b60028101805460036001600160a81b031990911633610100810291909117821790925542908301556040805184815260208101929092527f5ce4da5d68dc5d101354ada45397da559babc1484501c211993a7386a8b2cb7a91015b60405180910390a15050565b600082815260208190526040902054156102085760405162461bcd60e51b81526020600482015260126024820152714365206c6f74206578697374652064656a6160701b6044820152606401610147565b6040805160a08101825283815260208082018481526000838501819052336060850152426080850152868152918290529290208151815591519091906001820190610253908261079e565b50604082015160028201805460ff19166001836003811115610277576102776106a1565b021790555060608201516002820180546001600160a01b0390921661010002610100600160a81b03199092169190911790556080909101516003909101556040517f88a1f51b0e573b3fcefb25ab279d55cc3e09512e4b01e1ef05f4cbc94da66ef0906101ab908490849061085e565b600081815260208190526040812090600282015460ff16600381111561030f5761030f6106a1565b1461036b5760405162461bcd60e51b815260206004820152602660248201527f4c65206c6f7420646f69742065747265206372656520706172206c652067726f60448201526573736973746560d01b6064820152608401610147565b600281018054610100339081026001600160a81b0319909216919091176001179091554260038301556040805184815260208101929092527fab64cea60a5a37e24aa9da0c2e131225df7aea537ed2593f7f84228440e2694591016101ab565b60008181526020819052604090206001600282015460ff1660038111156103f4576103f46106a1565b1461044f5760405162461bcd60e51b815260206004820152602560248201527f4c65206c6f7420646f697420657472652076616c69646520706172206c27686f6044820152641c1a5d185b60da1b6064820152608401610147565b600281810180546001600160a81b0319163361010081029190911790921790554260038301556040805184815260208101929092527ff0c632a192fe5e358387003a0a82042941749e3b08f283d212bd69385db87b7c91016101ab565b600060208190529081526040902080546001820180549192916104ce90610715565b80601f01602080910402602001604051908101604052809291908181526020018280546104fa90610715565b80156105475780601f1061051c57610100808354040283529160200191610547565b820191906000526020600020905b81548152906001019060200180831161052a57829003601f168201915b505050506002830154600390930154919260ff8116926101009091046001600160a01b0316915085565b60006020828403121561058357600080fd5b5035919050565b634e487b7160e01b600052604160045260246000fd5b600080604083850312156105b357600080fd5b82359150602083013567ffffffffffffffff808211156105d257600080fd5b818501915085601f8301126105e657600080fd5b8135818111156105f8576105f861058a565b604051601f8201601f19908116603f011681019083821181831017156106205761062061058a565b8160405282815288602084870101111561063957600080fd5b8260208601602083013760006020848301015280955050505050509250929050565b6000815180845260005b8181101561068157602081850181015186830182015201610665565b506000602082860101526020601f19601f83011685010191505092915050565b634e487b7160e01b600052602160045260246000fd5b85815260a0602082015260006106d060a083018761065b565b9050600485106106f057634e487b7160e01b600052602160045260246000fd5b60408201949094526001600160a01b0392909216606083015260809091015292915050565b600181811c9082168061072957607f821691505b60208210810361074957634e487b7160e01b600052602260045260246000fd5b50919050565b601f82111561079957600081815260208120601f850160051c810160208610156107765750805b601f850160051c820191505b8181101561079557828155600101610782565b5050505b505050565b815167ffffffffffffffff8111156107b8576107b861058a565b6107cc816107c68454610715565b8461074f565b602080601f83116001811461080157600084156107e95750858301515b600019600386901b1c1916600185901b178555610795565b600085815260208120601f198616915b8281101561083057888601518255948401946001909101908401610811565b508582101561084e5787850151600019600388901b60f8161c191681555b5050505050600190811b01905550565b828152604060208201526000610877604083018461065b565b94935050505056fea2646970667358221220102a1f3185a9ad0c70bd43e2af21640f4636206df23043e4cdb8e730f707aff164736f6c63430008150033";

    public static final String FUNC_LOTS = "lots";

    public static final String FUNC_CREERLOT = "creerLot";

    public static final String FUNC_VALIDERRECEPTIONHOPITAL = "validerReceptionHopital";

    public static final String FUNC_METTREENPHARMACIE = "mettreEnPharmacie";

    public static final String FUNC_ADMINISTRERPATIENT = "administrerPatient";

    public static final Event ADMINISTRATION_EVENT = new Event("Administration",
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
            }, new TypeReference<Address>() {
            }));;

    public static final Event NOUVEAULOT_EVENT = new Event("NouveauLot",
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
            }, new TypeReference<Utf8String>() {
            }));;

    public static final Event RECEPTIONPHARMACIE_EVENT = new Event("ReceptionPharmacie",
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
            }, new TypeReference<Address>() {
            }));;

    public static final Event VALIDATIONHOPITAL_EVENT = new Event("ValidationHopital",
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
            }, new TypeReference<Address>() {
            }));;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("5777", "0x1B6fF0460Ff442120B9AA4e742E2D93CEe8ab15d");
    }

    @Deprecated
    protected SupplyChain(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected SupplyChain(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected SupplyChain(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected SupplyChain(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<AdministrationEventResponse> getAdministrationEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ADMINISTRATION_EVENT,
                transactionReceipt);
        ArrayList<AdministrationEventResponse> responses = new ArrayList<AdministrationEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AdministrationEventResponse typedResponse = new AdministrationEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.infirmier = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static AdministrationEventResponse getAdministrationEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ADMINISTRATION_EVENT, log);
        AdministrationEventResponse typedResponse = new AdministrationEventResponse();
        typedResponse.log = log;
        typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.infirmier = (String) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<AdministrationEventResponse> administrationEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getAdministrationEventFromLog(log));
    }

    public Flowable<AdministrationEventResponse> administrationEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ADMINISTRATION_EVENT));
        return administrationEventFlowable(filter);
    }

    public static List<NouveauLotEventResponse> getNouveauLotEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(NOUVEAULOT_EVENT,
                transactionReceipt);
        ArrayList<NouveauLotEventResponse> responses = new ArrayList<NouveauLotEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NouveauLotEventResponse typedResponse = new NouveauLotEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.nom = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static NouveauLotEventResponse getNouveauLotEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(NOUVEAULOT_EVENT, log);
        NouveauLotEventResponse typedResponse = new NouveauLotEventResponse();
        typedResponse.log = log;
        typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.nom = (String) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<NouveauLotEventResponse> nouveauLotEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getNouveauLotEventFromLog(log));
    }

    public Flowable<NouveauLotEventResponse> nouveauLotEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NOUVEAULOT_EVENT));
        return nouveauLotEventFlowable(filter);
    }

    public static List<ReceptionPharmacieEventResponse> getReceptionPharmacieEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(RECEPTIONPHARMACIE_EVENT,
                transactionReceipt);
        ArrayList<ReceptionPharmacieEventResponse> responses = new ArrayList<ReceptionPharmacieEventResponse>(
                valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ReceptionPharmacieEventResponse typedResponse = new ReceptionPharmacieEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.pharmacien = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ReceptionPharmacieEventResponse getReceptionPharmacieEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(RECEPTIONPHARMACIE_EVENT, log);
        ReceptionPharmacieEventResponse typedResponse = new ReceptionPharmacieEventResponse();
        typedResponse.log = log;
        typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.pharmacien = (String) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<ReceptionPharmacieEventResponse> receptionPharmacieEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getReceptionPharmacieEventFromLog(log));
    }

    public Flowable<ReceptionPharmacieEventResponse> receptionPharmacieEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RECEPTIONPHARMACIE_EVENT));
        return receptionPharmacieEventFlowable(filter);
    }

    public static List<ValidationHopitalEventResponse> getValidationHopitalEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(VALIDATIONHOPITAL_EVENT,
                transactionReceipt);
        ArrayList<ValidationHopitalEventResponse> responses = new ArrayList<ValidationHopitalEventResponse>(
                valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ValidationHopitalEventResponse typedResponse = new ValidationHopitalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.hopital = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ValidationHopitalEventResponse getValidationHopitalEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(VALIDATIONHOPITAL_EVENT, log);
        ValidationHopitalEventResponse typedResponse = new ValidationHopitalEventResponse();
        typedResponse.log = log;
        typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.hopital = (String) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<ValidationHopitalEventResponse> validationHopitalEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getValidationHopitalEventFromLog(log));
    }

    public Flowable<ValidationHopitalEventResponse> validationHopitalEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(VALIDATIONHOPITAL_EVENT));
        return validationHopitalEventFlowable(filter);
    }

    public RemoteFunctionCall<Tuple5<BigInteger, String, BigInteger, String, BigInteger>> lots(
            BigInteger param0) {
        final Function function = new Function(FUNC_LOTS,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }, new TypeReference<Utf8String>() {
                }, new TypeReference<Uint8>() {
                }, new TypeReference<Address>() {
                }, new TypeReference<Uint256>() {
                }));
        return new RemoteFunctionCall<Tuple5<BigInteger, String, BigInteger, String, BigInteger>>(function,
                new Callable<Tuple5<BigInteger, String, BigInteger, String, BigInteger>>() {
                    @Override
                    public Tuple5<BigInteger, String, BigInteger, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple5<BigInteger, String, BigInteger, String, BigInteger>(
                                (BigInteger) results.get(0).getValue(),
                                (String) results.get(1).getValue(),
                                (BigInteger) results.get(2).getValue(),
                                (String) results.get(3).getValue(),
                                (BigInteger) results.get(4).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> creerLot(BigInteger _id, String _nom) {
        final Function function = new Function(
                FUNC_CREERLOT,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_id),
                        new org.web3j.abi.datatypes.Utf8String(_nom)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> validerReceptionHopital(BigInteger _id) {
        final Function function = new Function(
                FUNC_VALIDERRECEPTIONHOPITAL,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_id)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> mettreEnPharmacie(BigInteger _id) {
        final Function function = new Function(
                FUNC_METTREENPHARMACIE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_id)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> administrerPatient(BigInteger _id) {
        final Function function = new Function(
                FUNC_ADMINISTRERPATIENT,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_id)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static SupplyChain load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new SupplyChain(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static SupplyChain load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SupplyChain(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static SupplyChain load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new SupplyChain(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static SupplyChain load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new SupplyChain(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<SupplyChain> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(SupplyChain.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<SupplyChain> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SupplyChain.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    public static RemoteCall<SupplyChain> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(SupplyChain.class, web3j, transactionManager, contractGasProvider,
                getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<SupplyChain> deploy(Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SupplyChain.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(),
                "");
    }

    private static String getDeploymentBinary() {
        return BINARY;
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class AdministrationEventResponse extends BaseEventResponse {
        public BigInteger id;

        public String infirmier;
    }

    public static class NouveauLotEventResponse extends BaseEventResponse {
        public BigInteger id;

        public String nom;
    }

    public static class ReceptionPharmacieEventResponse extends BaseEventResponse {
        public BigInteger id;

        public String pharmacien;
    }

    public static class ValidationHopitalEventResponse extends BaseEventResponse {
        public BigInteger id;

        public String hopital;
    }
}
