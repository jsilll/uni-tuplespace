package pt.ulisboa.tecnico.tuplespaces.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.ulisboa.tecnico.tuplespaces.contract.user.TupleSpacesServiceGrpc;
import pt.ulisboa.tecnico.tuplespaces.contract.user.TupleSpacesServiceGrpc.TupleSpacesServiceBlockingStub;

public class Client {
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s host port%n", Client.class.getName());
			return;
		}

		final String host = args[0];
		final int port = Integer.parseInt(args[1]);
		final String target = host + ":" + port;

		System.out.println(Client.class.getSimpleName() + " started!");
		
		final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		final TupleSpacesServiceBlockingStub stub = TupleSpacesServiceGrpc.newBlockingStub(channel);

		// TODO: Implement client code here

		channel.shutdownNow();
	}

}