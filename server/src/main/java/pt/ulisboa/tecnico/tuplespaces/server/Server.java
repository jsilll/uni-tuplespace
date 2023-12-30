package pt.ulisboa.tecnico.tuplespaces.server;

import java.util.Arrays;

import io.grpc.ServerBuilder;
import io.grpc.BindableService;

public class Server {
	public static class State {
		private boolean active;
		private TupleSpace space;

		State(boolean active) {
			this.active = active;
			this.space = new TupleSpace();
		}

		public boolean isActive() {
			return this.active;
		}

		public TupleSpace getTupleSpace() {
			return this.space;
		}

		synchronized public void setActive(boolean active) {
			this.active = active;
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", Server.class.getName());
			return;
		}

		final int port = Integer.parseInt(args[0]);
		final boolean debug = Arrays.asList(args).contains("--debug");

		State state = new State(true);
		final BindableService user_service = new UserServiceImpl(state);
		final BindableService admin_service = new AdminServiceImpl(state);

		final io.grpc.Server server = ServerBuilder.forPort(port).addService(user_service).addService(admin_service)
				.build();
		server.start();

		System.out.println("Server started on port " + port);
		server.awaitTermination();
	}
}
