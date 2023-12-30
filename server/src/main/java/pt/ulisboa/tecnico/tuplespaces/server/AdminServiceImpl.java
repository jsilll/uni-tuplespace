package pt.ulisboa.tecnico.tuplespaces.server;

import java.util.Arrays;
import java.util.stream.Collectors;

import io.grpc.stub.StreamObserver;

import pt.ulisboa.tecnico.tuplespaces.contract.admin.AdminServiceGrpc.AdminServiceImplBase;
import pt.ulisboa.tecnico.tuplespaces.contract.Common.Tuple;
import pt.ulisboa.tecnico.tuplespaces.contract.Common.TupleSpacesState;
import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.ActivateRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.ActivateResponse;

import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.DeactivateRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.DeactivateResponse;

import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.TupleSpacesStateRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.TupleSpacesStateResponse;

public class AdminServiceImpl extends AdminServiceImplBase {
    private Server.State state;

    public AdminServiceImpl(Server.State state) {
        super();
        this.state = state;
    }

    @Override
    public void activate(ActivateRequest req, StreamObserver<ActivateResponse> obs) {
        boolean previous = this.state.setActive(true);
        obs.onNext(ActivateResponse.newBuilder().setPreviousState(previous).build());
        obs.onCompleted();
    }

    @Override
    public void deactivate(DeactivateRequest req, StreamObserver<DeactivateResponse> obs) {
        boolean previous = this.state.setActive(false);
        obs.onNext(DeactivateResponse.newBuilder().setPreviousState(previous).build());
        obs.onCompleted();
    }

    @Override
    public void getTupleSpacesState(TupleSpacesStateRequest request,
            StreamObserver<TupleSpacesStateResponse> responseObserver) {
        TupleSpacesStateResponse response = TupleSpacesStateResponse.newBuilder()
                .setState(TupleSpacesState.newBuilder().addAllState(this.state.getTupleSpace().read().stream()
                        .map(tuple -> Tuple.newBuilder()
                                .addAllFields(Arrays.asList(tuple)).build())
                        .collect(Collectors.toList())))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}