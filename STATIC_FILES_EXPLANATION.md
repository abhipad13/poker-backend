# How Spring Boot Serves Static Files

## Automatic Static Resource Serving

Spring Boot **automatically** serves static files from these locations (in order of priority):
1. `src/main/resources/static/` (your files are here)
2. `src/main/resources/public/`
3. `src/main/resources/resources/`
4. `src/main/resources/META-INF/resources/`

## Request Flow

### Example 1: Requesting a static asset
```
Browser Request: GET /assets/index-e6861dc8.js
    ↓
Spring Boot DispatcherServlet
    ↓
Checks if it's an API endpoint (/api/**) → NO
    ↓
Checks if it's a WebSocket endpoint (/ws) → NO
    ↓
Spring Boot's ResourceHttpRequestHandler (auto-configured)
    ↓
Looks in: src/main/resources/static/assets/index-e6861dc8.js
    ↓
File found! → Returns file with proper Content-Type
    ↓
Browser receives: JavaScript file
```

### Example 2: Requesting the SPA route
```
Browser Request: GET /lobby/abc123
    ↓
Spring Boot DispatcherServlet
    ↓
Checks if it's an API endpoint (/api/**) → NO
    ↓
Checks if it's a WebSocket endpoint (/ws) → NO
    ↓
Checks if it's a static file (has a dot, like .js, .css) → NO
    ↓
WebConfig.java matches pattern: /{p1:[^\\.]*}/{p2:[^\\.]*}
    ↓
Forwards to: /index.html
    ↓
ResourceHttpRequestHandler serves: src/main/resources/static/index.html
    ↓
Browser receives: index.html (React Router handles /lobby/abc123 client-side)
```

## How It Works Under the Hood

### 1. Spring Boot Auto-Configuration
When you include `spring-boot-starter-web`, Spring Boot automatically:
- Configures a `ResourceHttpRequestHandler`
- Maps static resource locations
- Sets up proper MIME types
- Handles caching headers

### 2. Your WebConfig.java
Your `WebConfig.java` only handles **SPA routing** (forwarding routes to index.html).
It does NOT handle static file serving - that's automatic!

```java
// This only handles SPA routes, NOT static files
registry.addViewController("/{path:[^\\.]*}").setViewName("forward:/index.html");
// The regex [^\\.]* means "no dots" - so it won't match /assets/index.js
```

### 3. Request Matching Priority
Spring Boot checks in this order:
1. **Controller mappings** (like `/api/**`)
2. **WebSocket endpoints** (like `/ws`)
3. **Static resources** (files in `static/` folder)
4. **View controllers** (your WebConfig.java SPA routing)

## Testing Static File Serving

You can verify static files are being served:

```bash
# Test index.html
curl http://localhost:8080/

# Test JavaScript file
curl http://localhost:8080/assets/index-e6861dc8.js

# Test CSS file
curl http://localhost:8080/assets/index-d53e80be.css
```

## Customizing Static Resource Serving

If you needed to customize (you don't need to), you could add to WebConfig:

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/");
}
```

But this is **already the default**, so you don't need this code!

## Summary

- ✅ Static files are served **automatically** from `src/main/resources/static/`
- ✅ No custom code needed - Spring Boot handles it
- ✅ Your `WebConfig.java` only handles SPA routing (forwarding routes to index.html)
- ✅ Static assets (JS, CSS, images) are served directly without any forwarding
- ✅ The SPA routing regex `[^\\.]*` ensures asset requests (with dots) bypass the SPA routing
