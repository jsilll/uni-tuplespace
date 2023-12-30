package pt.ulisboa.tecnico.tuplespaces.server;

import java.util.Arrays;

import io.grpc.stub.StreamObserver;

import pt.ulisboa.tecnico.tuplespaces.contract.Common.Tuple;
import pt.ulisboa.tecnico.tuplespaces.contract.user.UserServiceGrpc.UserServiceImplBase;

import pt.ulisboa.tecnico.tuplespaces.contract.user.User.AddRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.user.User.AddResponse;

import pt.ulisboa.tecnico.tuplespaces.contract.user.User.ReadRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.user.User.ReadResponse;

import pt.ulisboa.tecnico.tuplespaces.contract.user.User.TakeRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.user.User.TakeResponse;

public class UserServiceImpl extends UserServiceImplBase {
    private Server.State state;

    public UserServiceImpl(Server.State state) {
        super();
        this.state = state;
    }

    @Override
    public void add(AddRequest request, StreamObserver<AddResponse> responseObserver) {
        if (!this.state.isActive()) {
            responseObserver.onNext(AddResponse.newBuilder().setActive(false).build());
            responseObserver.onCompleted();
            return;
        }

        final String[] tuple = request.getNewTuple().getFieldsList().toArray(new String[0]);
        this.state.getTupleSpace().add(tuple);
        responseObserver.onNext(AddResponse.newBuilder().setActive(true).build());
        responseObserver.onCompleted();
    }

    @Override
    public void read(ReadRequest request, StreamObserver<ReadResponse> responseObserver) {
        if (!this.state.isActive()) {
            responseObserver.onNext(ReadResponse.newBuilder().setActive(false).build());
            responseObserver.onCompleted();
            return;
        }

        final String[] tuple = request.getTemplate().getFieldsList().toArray(new String[0]);
        final String[] result = this.state.getTupleSpace().read(tuple);

        if (result == null) {
            responseObserver.onNext(ReadResponse.newBuilder().setActive(true).build());
            responseObserver.onCompleted();
            return;
        }

        responseObserver
                .onNext(ReadResponse.newBuilder().setTuple(Tuple.newBuilder().addAllFields(Arrays.asList(result)))
                        .build());
        responseObserver.onCompleted();
    }

    @Override
    public void take(TakeRequest request, StreamObserver<TakeResponse> responseObserver) {
        if (!this.state.isActive()) {
            responseObserver.onNext(TakeResponse.newBuilder().setActive(false).build());
            responseObserver.onCompleted();
            return;
        }

        final String[] tuple = request.getTemplate().getFieldsList().toArray(new String[0]);
        final String[] result = this.state.getTupleSpace().take(tuple);

        if (result == null) {
            responseObserver.onNext(TakeResponse.newBuilder().setActive(true).build());
            responseObserver.onCompleted();
            return;
        }

        responseObserver
                .onNext(TakeResponse.newBuilder().setTuple(Tuple.newBuilder().addAllFields(Arrays.asList(result)))
                        .build());
        responseObserver.onCompleted();
    }

}
