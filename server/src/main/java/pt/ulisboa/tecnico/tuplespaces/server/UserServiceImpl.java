package pt.ulisboa.tecnico.tuplespaces.server;

import io.grpc.stub.StreamObserver;

import pt.ulisboa.tecnico.tuplespaces.contract.user.UserServiceGrpc.UserServiceImplBase;

import pt.ulisboa.tecnico.tuplespaces.contract.user.User.AddRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.user.User.AddResponse;

import pt.ulisboa.tecnico.tuplespaces.contract.user.User.ReadRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.user.User.ReadResponse;

import pt.ulisboa.tecnico.tuplespaces.contract.user.User.TakeRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.user.User.TakeResponse;

public class UserServiceImpl extends UserServiceImplBase {
    @Override
    public void add(AddRequest request, StreamObserver<AddResponse> responseObserver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void read(ReadRequest request, StreamObserver<ReadResponse> responseObserver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void take(TakeRequest request, StreamObserver<TakeResponse> responseObserver) {
        throw new UnsupportedOperationException();
    }

}
