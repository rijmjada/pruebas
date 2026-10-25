# 🚀 Guía Paso a Paso: Jenkins Local + GitHub + Pipeline Docker

Esta guía te acompañará desde la creación de tu repositorio en GitHub hasta tener Jenkins corriendo localmente y ejecutando tu pipeline.

---

## 📋 Resumen del Flujo
1. Subir el proyecto a un nuevo repositorio de GitHub.
2. Levantar Jenkins localmente con Docker (configurado en el puerto **8085** para no chocar con el puerto **8080** de tu aplicación).
3. Desbloquear e inicializar Jenkins.
4. Crear el Job de tipo **Pipeline** que apuntará a tu GitHub y ejecutará el `Jenkinsfile`.

---

## Paso 1: Crear el repositorio en GitHub y subir tu código

1. Ve a [GitHub](https://github.com/) e inicia sesión.
2. Haz clic en el botón verde **New** (Nuevo repositorio).
3. Dale un nombre (por ejemplo `demo-spring-docker`) y elige si quieres que sea **Público** o **Privado**.
   > *Recomendación:* Deja desmarcadas las opciones de añadir README, .gitignore o licencia, ya que el proyecto ya tiene sus propios archivos.
4. Abre una terminal (PowerShell o CMD) en la carpeta de este proyecto (`demo`) y ejecuta los siguientes comandos:

```bash
# 1. Inicializar git localmente
git init

# 2. Agregar todos los archivos al commit inicial
git add .

# 3. Crear el primer commit
git commit -m "feat: setup inicial spring boot, docker y jenkinsfile"

# 4. Asegurarse de que la rama principal se llame 'main'
git branch -M main

# 5. Conectar con tu repositorio remoto de GitHub (reemplaza con tu URL)
git remote add origin https://github.com/TU_USUARIO/TU_REPOSITORIO.git

# 6. Subir el código
git push -u origin main
```

---

## Paso 2: Levantar Jenkins localmente con Docker

Para que Jenkins pueda ejecutar comandos de Docker (como `docker build`), ya dejamos creados los archivos necesarios:
- `Dockerfile.jenkins` (instala el cliente de Docker dentro de Jenkins).
- `compose-jenkins.yaml` (monta el socket de Docker y expone Jenkins en el puerto **8085**).

En tu terminal, ejecuta:

```bash
docker compose -f compose-jenkins.yaml up -d --build
```

Esto descargará la imagen, compilará el cliente de Docker y dejará el contenedor `jenkins-local` corriendo en segundo plano.

---

## Paso 3: Desbloquear Jenkins

1. Para obtener la contraseña inicial de administrador, ejecuta en tu terminal:

```bash
docker logs jenkins-local
```

Busca un bloque de texto que se ve similar a esto y **copia la clave alfanumérica**:

```text
*************************************************************
Jenkins initial setup is required. An admin user has been created...
Please use the following password to proceed to installation:

4a7b9c1d2e3f4051a8b9c0d1e2f3a4b5

*************************************************************
```

2. Abre tu navegador web e ingresa a:
   👉 **http://localhost:8085**
3. Pega la contraseña copiada y haz clic en **Continue**.
4. En la siguiente pantalla, selecciona **"Install suggested plugins"** (Instalar plugins sugeridos). Espera unos minutos mientras se instalan Git, Pipeline, etc.
5. Crea tu primer usuario administrador (nombre, contraseña y correo) y guarda los cambios.
6. En la URL de Jenkins, confirma que sea `http://localhost:8085/` y presiona **Save and Finish**.

---

## Paso 4: Configurar credenciales (Solo si tu repositorio es PRIVADO)

*Si tu repositorio es **Público**, puedes saltar este paso.*

Si tu repositorio es **Privado**, Jenkins necesita un token de GitHub para poder clonarlo:

1. En GitHub: ve a **Settings** (tu perfil) > **Developer settings** > **Personal access tokens** > **Tokens (classic)**.
2. Genera un nuevo token con el permiso **`repo`** marcado y copia el token generado.
3. En Jenkins:
   - Ve a **Administrar Jenkins** (*Manage Jenkins*) > **Credentials** > **System** > **Global credentials (unrestricted)**.
   - Haz clic en **Add Credentials**.
   - Kind: **Username with password**.
   - Username: Tu nombre de usuario de GitHub.
   - Password: El Personal Access Token que generaste.
   - ID: `github-credentials` (o el nombre que prefieras).
   - Haz clic en **Create**.

---

## Paso 5: Crear el Job de Pipeline en Jenkins

1. En el panel principal de Jenkins, haz clic en **Nueva Tarea** (*New Item*).
2. Ingresa un nombre para tu tarea (por ejemplo `demo-app-pipeline`).
3. Selecciona la opción **Pipeline** y haz clic en **OK**.
4. En la configuración del proyecto, baja hasta la sección **Pipeline**:
   - En **Definition**, selecciona: `Pipeline script from SCM`.
   - En **SCM**, selecciona: `Git`.
   - En **Repository URL**, pega la URL HTTPS de tu repositorio de GitHub:
     `https://github.com/TU_USUARIO/TU_REPOSITORIO.git`
   - En **Credentials**:
     - Si es público: selecciona `- none -`.
     - Si es privado: selecciona la credencial que creaste en el paso 4.
   - En **Branches to build** > **Branch Specifier**, escribe: `*/main`.
   - En **Script Path**, asegúrate de que esté escrito: `Jenkinsfile`.
5. Haz clic en **Guardar** (*Save*).

---

## Paso 6: Ejecutar tu Pipeline y verificar

1. En la página del proyecto recién creado, haz clic en **Construir con parámetros** (*Build with Parameters*).
   > *Nota:* La primera vez puede llamarse solo **Construir ahora** (*Build Now*); al ejecutarse por primera vez leerá los parámetros del `Jenkinsfile`.
2. Verás los parámetros:
   - `SKIP_TESTS`: Marcado por defecto en `true` (útil si aún no tienes MySQL corriendo en Jenkins).
   - `IMAGE_NAME`: `demo-app`.
3. Haz clic en **Construir** (*Build*).
4. Haz clic en el número de build (por ejemplo `#1`) y luego en **Console Output** para ver la ejecución paso a paso en tiempo real.
5. Al finalizar exitosamente (*SUCCESS*), abre tu terminal local y comprueba que la imagen Docker se ha creado:

```bash
docker images
```

Verás listadas `demo-app:1` y `demo-app:latest`.

---

## 🛠️ Comandos útiles para el día a día

| Acción | Comando |
| :--- | :--- |
| **Ver logs de Jenkins** | `docker logs -f jenkins-local` |
| **Detener Jenkins** | `docker compose -f compose-jenkins.yaml stop` |
| **Reanudar Jenkins** | `docker compose -f compose-jenkins.yaml start` |
| **Apagar y limpiar contenedor** | `docker compose -f compose-jenkins.yaml down` |
