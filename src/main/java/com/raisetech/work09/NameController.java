package com.raisetech.work09;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@RestController
@Validated
public class NameController {
    private final NameService nameService;


    public NameController(NameService nameService) {
        this.nameService = nameService;
    }

    @GetMapping("/names")
    public List<Name> getNames() {
        return nameService.findAll();
    }

    @GetMapping("/postcodes")
    public String response() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://map.yahooapis.jp/search/zip/V1/zipCodeSearch?query=105-0011&appid=dj00aiZpPWdpNUJZcWhVN2Q4NyZzPWNvbnN1bWVyc2VjcmV0Jng9NjU-"; //IDはpush時削除
        restTemplate.getMessageConverters()
                .stream()
                .filter(StringHttpMessageConverter.class::isInstance)
                .map(StringHttpMessageConverter.class::cast)
                .forEach(converter -> converter.setDefaultCharset(StandardCharsets.UTF_8));
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String json = new String(response.getBody().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        return json;
    }

    @GetMapping("/names/{id}")
    public Name findById(
            @PathVariable(value = "id")
            int id) throws Exception {
        return nameService.findById(id);

    }

    @PostMapping("/names")
    public ResponseEntity<Map<String, String>> postUser(@RequestBody @Validated CreateForm form, UriComponentsBuilder uriComponentsBuilder) {
        Name entity = form.convertToNameEntity();
        nameService.createName(entity);
        int id = entity.getId();
        String name = entity.getName();
        URI url = uriComponentsBuilder.path("/names/" + id).build().toUri();
        return ResponseEntity.created(url).body(Map.of("message", "name:" + name + " was successfully registered"));
    }

    @PatchMapping("/names/{id}")
    public ResponseEntity<Map<String, String>> patchById(
            @RequestBody @Validated UpdateForm form,
            @PathVariable(value = "id") int id) throws Exception {
        Name entity = form.convertToNameEntity();
        String name = entity.getName();
        nameService.patchById(id, name);
        return ResponseEntity.ok(Map.of("message", "id < " + id + " > was successfully updated to " + name));
    }

    @DeleteMapping("/names/{id}")
    public Map<String, String> deleteById(
            @PathVariable(value = "id")
            int id) throws Exception {
        nameService.deleteById(id);
        return Map.of("message", "id: " + id + " was successfully deleted");
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResourceFound(
            ResourceNotFoundException e, HttpServletRequest request) {

        Map<String, String> body = Map.of(
                "timestamp", ZonedDateTime.now().toString(),
                "status", String.valueOf(HttpStatus.NOT_FOUND.value()),
                "error", HttpStatus.NOT_FOUND.getReasonPhrase(),
                "message", e.getMessage(),
                "path", request.getRequestURI());
        return new ResponseEntity(body, HttpStatus.NOT_FOUND);
    }
}
