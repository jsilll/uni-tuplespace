package pt.ulisboa.tecnico.tuplespaces.admin;

import java.util.Arrays;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.ulisboa.tecnico.tuplespaces.contract.Common.Tuple;
import pt.ulisboa.tecnico.tuplespaces.contract.admin.AdminServiceGrpc;
import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.ActivateRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.ActivateResponse;
import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.DeactivateRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.DeactivateResponse;
import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.TupleSpacesStateRequest;
import pt.ulisboa.tecnico.tuplespaces.contract.admin.Admin.TupleSpacesStateResponse;
import pt.ulisboa.tecnico.tuplespaces.contract.admin.AdminServiceGrpc.AdminServiceBlockingStub;

public class Admin {
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s host port%n", Admin.class.getName());
			return;
		}

		final String host = args[0];
		final int port = Integer.parseInt(args[1]);
		final String target = host + ":" + port;
		final boolean debug = Arrays.asList(args).contains("--debug");

		final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		final AdminServiceBlockingStub stub = AdminServiceGrpc.newBlockingStub(channel);

		System.out.println(Admin.class.getSimpleName() + " connecting to " + target);

		while (true) {
			System.out.print("> ");
			final String[] command = System.console().readLine().split(" ");
			if (command[0].equals("")) {
				continue;
			}

			if (command[0].equals("exit")) {
				break;
			} else if (command[0].equals("activate")) {
				execActivate(stub);
			} else if (command[0].equals("deactivate")) {
				execDeactivate(stub);
			} else if (command[0].equals("get")) {
				execGetTupleSpaceState(stub);
			} else {
				System.err.println("Unknown command: " + command[0]);
			}
		}

		channel.shutdownNow();
	}

	private static void execActivate(AdminServiceBlockingStub stub) {
		final ActivateRequest req = ActivateRequest.newBuilder().build();
		final ActivateResponse res = stub.activate(req);
		final boolean prev = res.getPreviousState();
		System.out.println("Server: " + (prev ? "on" : "off")
				+ " -> on");
	}

	private static void execDeactivate(AdminServiceBlockingStub stub) {
		final DeactivateRequest req = DeactivateRequest.newBuilder().build();
		final DeactivateResponse res = stub.deactivate(req);
		final boolean prev = res.getPreviousState();
		System.out.println("Server: " + (prev ? "on" : "off")
				+ " -> off");
	}

	private static void execGetTupleSpaceState(AdminServiceBlockingStub stub) {
		final TupleSpacesStateRequest req = TupleSpacesStateRequest.newBuilder().build();
		final TupleSpacesStateResponse res = stub.getTupleSpacesState(req);
		System.out.print("TupleSpace: [");
		for (Tuple tuple : res.getState().getStateList()) {
			System.out.print("(" + tuple.getFields(0));
			for (String field : tuple.getFieldsList().subList(1, tuple.getFieldsCount())) {
				System.out.print(", " + field);
			}
			System.out.print("), ");
		}
		System.out.println("]");
	}
}