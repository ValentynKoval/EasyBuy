#!/usr/bin/env sh
set -eu

echo_info() {
  printf '%s\n' "$1"
}

if docker compose version >/dev/null 2>&1; then
  compose() { docker compose "$@"; }
elif command -v docker-compose >/dev/null 2>&1; then
  compose() { docker-compose "$@"; }
else
  echo "Docker Compose is required but was not found." >&2
  exit 1
fi

echo_info "Cleaning old containers and volumes..."
compose down -v --remove-orphans || true

# Remove project images if they exist.
docker rmi easybuy-postgres easybuy-redis easybuy-pgadmin >/dev/null 2>&1 || true
docker system prune -f

echo_info "Building and starting infrastructure..."
compose up -d --build

echo_info "Waiting for PostgreSQL readiness..."
until docker exec easybuy-postgres pg_isready -U postgres >/dev/null 2>&1; do
  sleep 2
done

echo_info "Waiting for Redis readiness..."
until docker exec easybuy-redis redis-cli ping >/dev/null 2>&1; do
  sleep 2
done

echo_info "Infrastructure is ready."
echo_info "Services:"
echo_info "  PostgreSQL: localhost:5432"
echo_info "  Redis: localhost:6379"
echo_info "  pgAdmin: http://localhost:8080"
echo_info ""
echo_info "Credentials:"
echo_info "  PostgreSQL: postgres/postgres"
echo_info "  pgAdmin: admin@admin.com/admin123"
