package pt.ulisboa.tecnico.tuplespaces.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.ulisboa.tecnico.tuplespaces.contract.user.UserServiceGrpc;
import pt.ulisboa.tecnico.tuplespaces.contract.user.UserServiceGrpc.UserServiceBlockingStub;

import pt.ulisboa.tecnico.tuplespaces.contract.Common.Tuple;

import pt.ulisboa.tecnico.tuplespaces.contract.user.User.AddRequest;

public class Client {
	private enum Command {
		ADD,
		READ,
		TAKE,
		EXIT,
	}

	private static final HashMap<String, Command> commands = new HashMap<String, Command>() {
		{
			put("add", Command.ADD);
			put("read", Command.READ);
			put("take", Command.TAKE);
			put("exit", Command.EXIT);
		}
	};

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s host port%n", Client.class.getName());
			return;
		}

		boolean debug = false;
		final String host = args[0];
		final int port = Integer.parseInt(args[1]);
		final String target = host + ":" + port;

		if (args.length > 2) {
			for (int i = 2; i < args.length; i++) {
				if (args[i].equals("--debug")) {
					debug = true;
					break;
				}
			}
		}

		final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		final UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);

		System.out.println(Client.class.getSimpleName() + " connecting to " + target);

		while (true) {
			System.out.print("> ");
			String[] command = System.console().readLine().split(" ");
			if (command[0].equals("")) {
				continue;
			}

			Command cmd = commands.get(command[0]);
			if (cmd == null) {
				System.out.println("Invalid command");
				continue;
			}

			switch (cmd) {
				case ADD:
					if (command.length == 1) {
						System.out.println("Invalid command: missing tuple");
						break;
					}

					List<String> fields = new ArrayList<String>();
					for (int i = 1; i < command.length; i++) {
						fields.add(command[i]);
					}

					AddRequest req = AddRequest.newBuilder().setNewTuple(Tuple.newBuilder().addAllFields(fields).build()).build();
					
					try {
						stub.add(req);
					} catch (Exception e) {
						System.out.println("Error: " + e.getMessage());
						break;
					}

					break;

				case READ:
					break;

				case TAKE:
					break;

				case EXIT:
					channel.shutdownNow();
					return;
			}
		}
	}
}