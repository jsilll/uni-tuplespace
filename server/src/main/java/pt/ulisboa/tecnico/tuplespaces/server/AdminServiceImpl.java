package pt.ulisboa.tecnico.tuplespaces.server;

import io.grpc.stub.StreamObserver;

import pt.ulisboa.tecnico.tuplespaces.contract.admin.AdminServiceGrpc.AdminServiceImplBase;

import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.ActivateRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.ActivateResponse;

import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.DeactivateRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.DeactivateResponse;

import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.TupleSpacesStateRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.TupleSpacesStateResponse;


public class AdminServiceImpl extends AdminServiceImplBase {
    private TupleSpace space;

    public AdminServiceImpl(TupleSpace space) {
        super();
        this.space = space;
    }

    @Override
    public void activate(ActivateRequest req, StreamObserver<ActivateResponse> obs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deactivate(DeactivateRequest req, StreamObserver<DeactivateResponse> obs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getTupleSpacesState(TupleSpacesStateRequest request,
            StreamObserver<TupleSpacesStateResponse> responseObserver) {
        throw new UnsupportedOperationException();
    }
}