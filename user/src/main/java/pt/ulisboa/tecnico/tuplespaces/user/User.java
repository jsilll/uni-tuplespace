package pt.ulisboa.tecnico.tuplespaces.user;

import java.util.Arrays;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.ulisboa.tecnico.tuplespaces.contract.user.UserServiceGrpc;
import pt.ulisboa.tecnico.tuplespaces.contract.user.UserServiceGrpc.UserServiceBlockingStub;

import pt.ulisboa.tecnico.tuplespaces.contract.Common.Tuple;

import pt.ulisboa.tecnico.tuplespaces.contract.user.User.AddRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.user.User.AddResponse;
import pt.ulisboa.tecnico.tuplespaces.contract.user.User.ReadRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.user.User.ReadResponse;
import pt.ulisboa.tecnico.tuplespaces.contract.user.User.TakeRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.user.User.TakeResponse;

public class User {
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s host port%n", User.class.getName());
			return;
		}

		final String host = args[0];
		final int port = Integer.parseInt(args[1]);
		final String target = host + ":" + port;
		final boolean debug = Arrays.asList(args).contains("--debug");

		final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		final UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);

		System.out.println(User.class.getSimpleName() + " connecting to " + target);

		while (true) {
			System.out.print("> ");
			final String[] command = System.console().readLine().split(" ");
			if (command[0].equals("")) {
				continue;
			}

			if (command[0].equals("exit")) {
				break;
			}

			if (command[0].equals("add")) {
				execAdd(stub, command);
				continue;
			} else if (command[0].equals("read")) {
				execRead(stub, command);
				continue;
			} else if (command[0].equals("take")) {
				execTake(stub, command);
				continue;
			} else {
				System.out.println("Invalid command");
				continue;
			}
		}
		
		channel.shutdownNow();
	}

	private static void execAdd(UserServiceBlockingStub stub, String[] command) {
		if (command.length == 1) {
			System.out.println("Invalid command: missing tuple");
			return;
		}

		final List<String> fields = Arrays.asList(command).subList(1, command.length);
		final AddRequest req = AddRequest.newBuilder().setNewTuple(Tuple.newBuilder().addAllFields(fields).build())
				.build();

		try {
			final AddResponse res = stub.add(req);
			if (res.getActive()) {
				System.out.println("Tuple added");
			} else {
				System.out.println("Server is not active");
			}
		} catch (Exception e) {
			System.out.println("Error: Could not connect to server");
		}
	}

	private static void execRead(UserServiceBlockingStub stub, String[] command) {
		if (command.length == 1) {
			System.out.println("Invalid command: missing search string");
			return;
		}

		final List<String> fields = Arrays.asList(command).subList(1, command.length);
		final ReadRequest req = ReadRequest.newBuilder().setTemplate(Tuple.newBuilder().addAllFields(fields).build())
				.build();

		try {
			final ReadResponse res = stub.read(req);
			if (res.getResultCase() == ReadResponse.ResultCase.TUPLE) {
				System.out.print("Tuple found: (" + res.getTuple().getFieldsList().get(0));
				for (String field : res.getTuple().getFieldsList().subList(1, res.getTuple().getFieldsCount())) {
					System.out.print(", " + field);
				}
				System.out.println(")");
			} else if (res.getResultCase() == ReadResponse.ResultCase.ACTIVE) {
				if (res.getActive()) {
					System.out.println("No tuple found");
				} else {
					System.out.println("Server is not active");
				}
			} else {
				System.out.println("Error: Server sent an invalid response");
			}
		} catch (Exception e) {
			System.out.println("Error: Could not connect to server");
		}
	}

	private static void execTake(UserServiceBlockingStub stub, String[] command) {
		if (command.length == 1) {
			System.out.println("Invalid command: missing search string");
			return;
		}

		final List<String> fields = Arrays.asList(command).subList(1, command.length);
		final TakeRequest req = TakeRequest.newBuilder().setTemplate(Tuple.newBuilder().addAllFields(fields).build())
				.build();

		try {
			final TakeResponse res = stub.take(req);
			if (res.getResultCase() == TakeResponse.ResultCase.TUPLE) {
				System.out.print("Tuple found: (" + res.getTuple().getFieldsList().get(0));
				for (String field : res.getTuple().getFieldsList().subList(1, res.getTuple().getFieldsCount())) {
					System.out.print(", " + field);
				}
				System.out.println(")");
			} else if (res.getResultCase() == TakeResponse.ResultCase.ACTIVE) {
				if (res.getActive()) {
					System.out.println("No tuple found");
				} else {
					System.out.println("Server is not active");
				}
			} else {
				System.out.println("Error: Server sent an invalid response");
			}
		} catch (Exception e) {
			System.out.println("Error: Could not connect to server");
		}
	}
}
