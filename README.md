# 🎾 Wimbledon 2026 — App de Pronósticos
**Singles Masculino · Full-Stack · Java + PostgreSQL + React**

---

## Stack
| Capa | Tecnología |
|------|-----------|
| Backend | Java 21 + Spring Boot 3.3 |
| Seguridad | Spring Security + JWT (jjwt 0.12) |
| Base de datos | PostgreSQL 15+ |
| ORM | Spring Data JPA (Hibernate) |
| Frontend | React 18 + Vite 5 |
| HTTP client | Axios |
| Router | React Router v6 |

---

## Estructura del proyecto
```
wimbledon/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/wimbledon/
│       ├── WimbledonApplication.java
│       ├── config/          ← SecurityConfig, GlobalExceptionHandler
│       ├── controller/      ← Auth, Match, Leaderboard, Tournament, Admin
│       ├── dto/             ← Request/Response DTOs
│       ├── entity/          ← User, Match, Pick, MatchResult, etc.
│       ├── repository/      ← JPA repos
│       ├── security/        ← JwtUtil, JwtFilter
│       └── service/         ← AuthService, PickService, ScoreService, etc.
└── frontend/
    ├── index.html
    ├── vite.config.js
    └── src/
        ├── App.jsx
        ├── main.jsx
        ├── index.css
        ├── context/         ← AuthContext (+ axios), ToastContext
        ├── components/      ← TopBar, BottomNav, MatchCard
        └── pages/           ← AuthPage, TodayPage, TablaPage, TorneoPage, AdminPage
```

---

## Requisitos previos
- Java 21+
- Maven 3.9+
- Node.js 20+
- PostgreSQL 15+

---

## Setup local

### 1. Base de datos
```sql
-- En psql o DBeaver:
CREATE DATABASE wimbledon;
```
El schema se aplica automáticamente al iniciar el backend (`schema.sql`).

### 2. Variables de entorno (backend)
Podés crear un archivo `.env` o configurar las variables del sistema:
```
DATABASE_URL=jdbc:postgresql://localhost:5432/wimbledon
DATABASE_USER=postgres
DATABASE_PASSWORD=tu_password
JWT_SECRET=una-clave-secreta-de-minimo-32-caracteres-aqui
```

O editá `application.yml` directamente para desarrollo local.

### 3. Correr el backend
```bash
cd backend
mvn spring-boot:run
# → Escucha en http://localhost:8080
```

### 4. Correr el frontend
```bash
cd frontend
npm install
npm run dev
# → Escucha en http://localhost:5173
# Proxy a /api → localhost:8080 configurado en vite.config.js
```

### 5. Usuario admin por defecto
```
Email:    admin@wimbledon.com
Password: admin1234
```
(Definido en `schema.sql` con BCrypt)

---

## API Endpoints

### Auth
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/register` | Registrar usuario |
| POST | `/api/auth/login` | Login → devuelve JWT |

### Partidos (requiere JWT)
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/matches/today` | Partidos de hoy con mi pick |
| POST | `/api/matches/{id}/pick` | Enviar/corregir pronóstico |

### Leaderboard
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/leaderboard` | Tabla de posiciones con puntos |

### Torneo
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/tournament/my-pick` | Mi pronóstico de torneo |
| POST | `/api/tournament/my-pick` | Guardar pronóstico de torneo |
| GET | `/api/tournament/result` | Resultado real del torneo |

### Admin (solo ADMIN)
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/admin/matches` | Crear partido |
| POST | `/api/admin/matches/{id}/result` | Cargar resultado |
| DELETE | `/api/admin/matches/{id}` | Eliminar partido |
| POST | `/api/admin/tournament/result` | Guardar campeón/semis reales |

---

## Sistema de puntos
| Acierto | Puntos |
|---------|--------|
| Ganador del partido | +1 |
| Resultado en sets | +3 adicionales |
| Resultado exacto en games | +10 adicionales |
| Campeón del torneo | +15 |
| Cada semifinalista acertado | +10 |

**Corrección diaria:** cada usuario puede corregir UN pronóstico por jornada, incluso después del cierre. Se registra en `daily_corrections`.

---

## Deploy en producción (Railway — recomendado)

### Backend
1. Subir el proyecto a GitHub
2. Crear proyecto en [railway.app](https://railway.app)
3. Agregar servicio **PostgreSQL** (Railway lo provisiona automáticamente)
4. Agregar servicio **Java** apuntando al repo, subdirectorio `backend/`
5. Configurar variables de entorno:
   ```
   DATABASE_URL=${PGURL}   # Railway lo inyecta automático
   JWT_SECRET=tu-secreto
   CORS_ORIGINS=https://tu-frontend.vercel.app
   ```

### Frontend
1. Deploy en [Vercel](https://vercel.com) o Netlify
2. Subdirectorio: `frontend/`
3. Build command: `npm run build`
4. Output dir: `dist`
5. Variable de entorno: `VITE_API_URL=https://tu-backend.railway.app`
6. Actualizar `vite.config.js` para usar `import.meta.env.VITE_API_URL` en prod

### Alternativa: todo en Railway
Podés servir el frontend buildeado desde Spring Boot:
```bash
# Build frontend y copiar a resources/static
cd frontend && npm run build
cp -r dist/* ../backend/src/main/resources/static/
```
Spring Boot sirve el SPA automáticamente.

---

## Flujo de uso

1. **Admin** crea los partidos del día desde el panel (con fecha, hora, cancha y jugadores)
2. **Jugadores** se registran y pronostican antes del cierre (30 min antes del primer partido)
3. **Admin** carga los resultados reales cuando terminan los partidos
4. **Tabla** se actualiza automáticamente con los puntos calculados
5. **Pronóstico de torneo** (campeón + semis) está disponible en cualquier momento

---

## Próximas mejoras sugeridas
- Push notifications cuando cargan resultados (Firebase FCM)
- Historial de pronósticos por jornada
- Avatar de usuario personalizable  
- Grupos privados con código de invitación
- Integración con la API oficial de Wimbledon para resultados automáticos
