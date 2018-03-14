package core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;

public class EventLoop extends AbstractVerticle {

	private SQLClient  mySQLClient;

	public void start(Future<Void> startFuture) {
		JsonObject config = new JsonObject().
				put("host", "localhost").
				put("port", 32779).
				put("username", "root").
				put("password", "root").
				put("database", "RaspBerry");
		mySQLClient = MySQLClient.createShared(vertx, config);

		Router router = Router.router(vertx);

		router.route("/api/*").handler(BodyHandler.create());
		router.post("/api/sensor").handler(this::setSensor);
		router.get("/api/riego/hora/:idArduino").handler(this::getRiegoHora);

		vertx.createHttpServer().requestHandler(router::accept).listen(8080, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});
	}

	private void setSensor(RoutingContext routingContext) {
		int idarduino = Integer.parseInt(routingContext.request().getParam("idArduino"));
		float temperatura = Float.parseFloat(routingContext.request().getParam("temperatura"));
		int humedads = Integer.parseInt(routingContext.request().getParam("humedadS"));
		int humedada = Integer.parseInt(routingContext.request().getParam("humedadA"));
		int fecha = Integer.parseInt(routingContext.request().getParam("fecha"));
		mySQLClient.getConnection(conn -> {
			if (conn.succeeded()) {
				try {
					conn.result().queryWithParams("INSERT INTO sensor(id_arduino,temperatura,humedadS,humedadA,fecha) VALUES (?,?,?,?,?)",
							new JsonArray().add(idarduino).add(temperatura).add(humedads).add(humedada).add(fecha), res -> {
						if (res.succeeded()) {
							System.out.println("\n\n\n________________________________________________");
							System.out.println("POST setSensor");
							routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8").end();

						} else {
							routingContext.response().setStatusCode(401).end("else 1");
						}
					});
				} catch (Exception e) {
					routingContext.response().setStatusCode(401).end( routingContext.getBodyAsString() + "catch 1");
				}
			} else {
				routingContext.response().setStatusCode(401).end("Else 2");
			}
		});
	}

	private void getRiegoHora(RoutingContext routingContext) {
		String paramStr = routingContext.request().getParam("idArduino");
		if (paramStr != null) {
			try {
				int param = Integer.parseInt(paramStr);
				
				mySQLClient.getConnection(conn -> {
					if (conn.succeeded()) {
						SQLConnection connection = conn.result();
						String query = "INSERT INTO sensor(id_arduino,temperatura,humedadS,humedadA,fecha) VALUES (?,?,?,?,?)";
						JsonArray paramQuery = new JsonArray()
								.add(param);
						connection.queryWithParams(
								query, 
								paramQuery, 
								res -> {
									if (res.succeeded()) {
										routingContext.response().end(Json.encodePrettily(res.result().getRows()));
									}else {
										routingContext.response().setStatusCode(400).end(
												"Error: " + res.cause());	
									}
								});
					}else {
						routingContext.response().setStatusCode(400).end(
								"Error: " + conn.cause());
					}
				});
				
				
				
				//routingContext.response().setStatusCode(200).
				//	end(Json.encodePrettily(database.get(param)));
			}catch (ClassCastException e) {
				routingContext.response().setStatusCode(400).end();
			}
		}else {
			routingContext.response().setStatusCode(400).end();
		}
	}

	/*private void getValuesByUserAndDate(RoutingContext routingContext) {
		mySQLClient.getConnection(conn -> {
			if (conn.succeeded()) {
				try {
					int id = Integer.parseInt(routingContext.request().getParam("user"));
					long timestamp = Long.parseLong(routingContext.request().getParam("timestamp"));
					String pattern = "yyyy-MM-dd HH:mm:ss";
					SimpleDateFormat formatter = new SimpleDateFormat(pattern);
					Date date = new Date(timestamp);
					String dateString = formatter.format(date);
					conn.result().queryWithParams("SELECT * FROM arduinorest.values WHERE user = ? AND uploadDate > ?;",
							new JsonArray().add(id).add(dateString), res -> {
								if (res.succeeded()) {
									ResultSet resultSet = res.result();
									routingContext.response()
											.putHeader("content-type", "application/json; charset=utf-8")
											.end(Json.encodePrettily(resultSet.getRows()));
								} else {
									routingContext.response().setStatusCode(401).end();
								}
							});
				} catch (Exception e) {
					routingContext.response().setStatusCode(401).end();
				}
			} else {
				routingContext.response().setStatusCode(401).end();
			}
		});

	}

	private void getLastValueByUser(RoutingContext routingContext) {
		mySQLClient.getConnection(conn -> {
			if (conn.succeeded()) {
				try {
					int id = Integer.parseInt(routingContext.request().getParam("user"));
					conn.result().queryWithParams(
							"SELECT * FROM arduinorest.values WHERE user = ? order by uploadDate DESC limit 1;",
							new JsonArray().add(id), res -> {
								if (res.succeeded()) {
									ResultSet resultSet = res.result();
									if (!resultSet.getRows().isEmpty()) {
										System.out.println("\n\n\n________________________________________________");
										System.out.println("GET");
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
										System.out.println("Hora de la petición: " + sdf.format(Calendar.getInstance().getTime()));
										System.out.println(routingContext.request().remoteAddress().toString());
										System.out.println(Json.encodePrettily(resultSet.getRows().get(0)));
										routingContext.response()
												.putHeader("content-type", "application/json; charset=utf-8")
												.end(Json.encodePrettily(resultSet.getRows().get(0)));
									}
								} else {
									routingContext.response().setStatusCode(401).end();
								}
							});
				} catch (Exception e) {
					routingContext.response().setStatusCode(401).end();
				}
			} else {
				routingContext.response().setStatusCode(401).end();
			}
		});

	}

	private void addValue(RoutingContext routingContext) {
		mySQLClient.getConnection(conn -> {
			if (conn.succeeded()) {
				try {
					final SensorDataImpl data = Json.decodeValue(routingContext.getBodyAsString(),
							SensorDataImpl.class);
					String pattern = "yyyy-MM-dd HH:mm:ss";
					SimpleDateFormat formatter = new SimpleDateFormat(pattern);
					String dateString = "";
					if (data.getUploadDate() != null){
						dateString = formatter.format(data.getUploadDate());
					}else{
						dateString = "2017-02-17T13:55:05.000";
					}
					conn.result().queryWithParams(
							"INSERT INTO arduinorest.values (value,user,uploadDate) VALUES (?,?,?)",
							new JsonArray().add(data.getValue()).add(data.getUser()).add(dateString), res -> {
								if (res.succeeded()) {
									System.out.println("\n\n\n________________________________________________");
									System.out.println("POST");
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
									System.out.println("Hora de la petición: " + sdf.format(Calendar.getInstance().getTime()));
									System.out.println(routingContext.request().remoteAddress().toString());
									routingContext.response().setStatusCode(201)
											.putHeader("content-type", "application/json; charset=utf-8").end();

								} else {
									routingContext.response().setStatusCode(401).end();
								}
							});
				} catch (Exception e) {
					routingContext.response().setStatusCode(401).end();
				}
			} else {
				routingContext.response().setStatusCode(401).end();
			}
		});

	}

	private void deleteValue(RoutingContext routingContext) {
		mySQLClient.getConnection(conn -> {
			if (conn.succeeded()) {
				try {
					final SensorDataImpl data = Json.decodeValue(routingContext.getBodyAsString(),
							SensorDataImpl.class);
					conn.result().queryWithParams("DELETE FROM arduinorest.values WHERE idvalue = ?",
							new JsonArray().add(data.getIdvalue()), res -> {
								if (res.succeeded()) {
									routingContext.response().setStatusCode(201)
											.putHeader("content-type", "application/json; charset=utf-8")
											.end(Json.encodePrettily(data));

								} else {
									routingContext.response().setStatusCode(401).end();
								}
							});
				} catch (Exception e) {
					routingContext.response().setStatusCode(401).end();
				}
			} else {
				routingContext.response().setStatusCode(401).end();
			}
		});

	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}
*/
}