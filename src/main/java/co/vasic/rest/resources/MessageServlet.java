package co.vasic.rest.resources;

import co.vasic.messages.Message;
import co.vasic.messages.MessageInterface;
import co.vasic.messages.MessageService;
import co.vasic.rest.HttpRequestInterface;
import co.vasic.rest.HttpResponse;
import co.vasic.rest.HttpResponseInterface;
import co.vasic.rest.HttpServlet;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageServlet extends HttpServlet {

    MessageService messageService;
    Gson g;
    Pattern p;

    public MessageServlet() {
        g = new Gson();
        p = Pattern.compile("/messages/(\\d+)/?");
        this.messageService = MessageService.getInstance();
    }

    @Override
    public HttpResponseInterface handleIndex(HttpRequestInterface request) {
        List<MessageInterface> messages = messageService.getMessages();

        return HttpResponse.builder()
                .headers(new HashMap<>() {{
                    put("Content-Type", "application/json");
                }})
                .body(g.toJson(messages))
                .build();
    }

    @Override
    public HttpResponseInterface handleGet(HttpRequestInterface request) {
        Matcher m = p.matcher(request.getPath());
        if (m.matches()) {
            int id = Integer.parseInt(m.group(1));
            Message message = (Message) messageService.getMessage(id);

            if (message != null) {
                return HttpResponse.builder()
                        .headers(new HashMap<>() {{
                            put("Content-Type", "application/json");
                        }})
                        .body(g.toJson(message))
                        .build();
            }
        }
        return HttpResponse.notFound();
    }

    @Override
    public HttpResponseInterface handlePost(HttpRequestInterface request) {
        Message message = (Message) messageService.addMessage(g.fromJson(request.getBody(), Message.class));

        if (message != null) {
            return HttpResponse.builder()
                    .headers(new HashMap<>() {{
                        put("Content-Type", "application/json");
                    }})
                    .body(g.toJson(message))
                    .build();
        }
        return HttpResponse.internalServerError();
    }

    @Override
    public HttpResponseInterface handlePut(HttpRequestInterface request) {
        Matcher m = p.matcher(request.getPath());
        if (m.matches()) {
            int id = Integer.parseInt(m.group(1));

            Message message = (Message) messageService.updateMessage(id, g.fromJson(request.getBody(), Message.class));
            if (message != null) {
                return HttpResponse.builder()
                        .headers(new HashMap<>() {{
                            put("Content-Type", "application/json");
                        }})
                        .body(g.toJson(message))
                        .build();
            }
        }
        return HttpResponse.notFound();
    }

    @Override
    public HttpResponseInterface handleDelete(HttpRequestInterface request) {
        Matcher m = p.matcher(request.getPath());
        if (m.matches()) {
            int id = Integer.parseInt(m.group(1));

            if (messageService.deleteMessage(id)) {
                return HttpResponse.ok();
            }
        }
        return HttpResponse.notFound();
    }
}
