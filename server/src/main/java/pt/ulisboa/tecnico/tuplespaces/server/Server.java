package pt.ulisboa.tecnico.tuplespaces.server;

import io.grpc.ServerBuilder;
import io.grpc.BindableService;

public class Server {
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", Server.class.getName());
			return;
		}

		final int port = Integer.parseInt(args[0]);
		final BindableService user_service = new UserServiceImpl();
		final BindableService admin_service = new AdminServiceImpl();

		final io.grpc.Server server = ServerBuilder.forPort(port).addService(user_service).addService(admin_service)
				.build();
		server.start();

		System.out.println("Server started on port " + port);
		server.awaitTermination();
	}
}
