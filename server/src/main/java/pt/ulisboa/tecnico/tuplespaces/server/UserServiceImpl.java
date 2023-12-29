package pt.ulisboa.tecnico.tuplespaces.server;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.grpc.stub.StreamObserver;

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

        // get a String[] 
        final List<String> fields = request.getNewTuple().getAllFields().entrySet().stream()
                .map(entry -> entry.getValue().toString()).collect(Collectors.toList());

        // this.state.getTupleSpace().add(*fields);

        // somehow call this.state.getTupleSpace().add(tuple);
        // and get the result

        responseObserver.onNext(AddResponse.newBuilder().setActive(true).build());
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
