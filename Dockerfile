FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/sri-rathna-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Click **"Commit changes"**

---

## Update Render Settings

1. Go to **Settings** → **Build & Deploy**:
```
   Runtime:          Docker
   Build Command:    (leave empty)
   Start Command:    (leave empty)
```

2. Click **"Save Changes"**

3. Go to **"Manual Deploy"** → **"Deploy latest commit"**

---

## Wait for Build (~5 minutes)

This time you should see in logs:
```
Successfully built...
Started SriRathnaApplication...
```

Then test:
```
https://sri-rathna-backend.onrender.com/api/bookings/booked-dates
