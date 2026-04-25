#!/usr/bin/env bash
set -euo pipefail

# One-time setup script for Ubuntu/Debian VPS.
# Usage:
#   chmod +x deploy/vps/bootstrap.sh
#   sudo APP_USER=<your-vps-user> ./deploy/vps/bootstrap.sh

APP_NAME=projectx
APP_USER=${APP_USER:-${SUDO_USER:-}}
APP_DIR=/opt/${APP_NAME}
ENV_FILE=${APP_DIR}/shared/.env
SERVICE_FILE=/etc/systemd/system/${APP_NAME}.service

if [[ -z "${APP_USER}" ]]; then
  echo "APP_USER is required. Example: sudo APP_USER=ubuntu ./deploy/vps/bootstrap.sh"
  exit 1
fi

if ! id "${APP_USER}" >/dev/null 2>&1; then
  echo "User '${APP_USER}' does not exist on this server."
  exit 1
fi

echo "Installing Java 21 runtime..."
apt-get update
apt-get install -y openjdk-21-jre-headless

echo "Creating app directories..."
mkdir -p "${APP_DIR}/releases"
mkdir -p "${APP_DIR}/shared"
mkdir -p /var/log/${APP_NAME}

chown -R "${APP_USER}:${APP_USER}" "${APP_DIR}"
chown -R "${APP_USER}:${APP_USER}" /var/log/${APP_NAME}

if [[ ! -f "${ENV_FILE}" ]]; then
  cat > "${ENV_FILE}" <<'EOF'
SPRING_PROFILES_ACTIVE=prod
APP_JWT_SECRET=replace-this-with-a-very-long-random-secret-key-at-least-32-bytes
APP_JWT_EXPIRATION_MINUTES=60
APP_SECURITY_CORS_ALLOWED_ORIGIN_PATTERNS=https://your-domain.com
APP_INFO_LOG_FILE=/var/log/projectx/projectx-info.log
APP_ERROR_LOG_FILE=/var/log/projectx/projectx-error.log
EOF

  chown "${APP_USER}:${APP_USER}" "${ENV_FILE}"
  chmod 600 "${ENV_FILE}"
fi

echo "Creating systemd service..."
cat > "${SERVICE_FILE}" <<EOF
[Unit]
Description=ProjectX Spring Boot Service
After=network.target

[Service]
Type=simple
User=${APP_USER}
Group=${APP_USER}
WorkingDirectory=${APP_DIR}/current
EnvironmentFile=${ENV_FILE}
ExecStart=/usr/bin/java -jar ${APP_DIR}/current/app.jar
SuccessExitStatus=143
Restart=always
RestartSec=5
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable ${APP_NAME}

echo "Bootstrap complete."
echo "Next steps:"
echo "1) Edit ${ENV_FILE} with real values (especially APP_JWT_SECRET)."
echo "2) Commit and push to main; GitHub Actions will deploy and restart ${APP_NAME}."
