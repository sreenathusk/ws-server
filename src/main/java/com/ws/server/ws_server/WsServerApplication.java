package com.ws.server.ws_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@SpringBootApplication
@EnableWebSocket
public class WsServerApplication implements WebSocketConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(WsServerApplication.class, args);
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new MyWebSocketHandler(), "/ws").setAllowedOriginPatterns("*");
	}

	@Bean
	public RestController restController() {
		return new RestController();
	}
}

class MyWebSocketHandler extends TextWebSocketHandler {

	private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.put(session.getId(), session);
		System.out.println("WebSocket connection established: " + session.getId());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		String timestampedMessage = payload + " - " + LocalDateTime.now().format(formatter);
		session.sendMessage(new TextMessage(timestampedMessage));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
		sessions.remove(session.getId());
		System.out.println("WebSocket connection closed: " + session.getId());
	}
}

@Controller
@RequestMapping("/api")
class RestController {

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@GetMapping("/get")
	public ResponseEntity<String> getRequest(@RequestParam(value = "message", defaultValue = "GET request") String message) {
		String timestampedMessage = message + " - " + LocalDateTime.now().format(formatter);
		return ResponseEntity.ok(timestampedMessage);
	}

	@PostMapping("/post")
	public ResponseEntity<String> postRequest(@RequestBody String message) {
		String timestampedMessage = message + " - " + LocalDateTime.now().format(formatter);
		return ResponseEntity.ok(timestampedMessage);
	}

	@PutMapping("/put")
	public ResponseEntity<String> putRequest(@RequestBody String message) {
		String timestampedMessage = message + " - " + LocalDateTime.now().format(formatter);
		return ResponseEntity.ok(timestampedMessage);
	}
}