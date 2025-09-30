terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }
}

provider "docker" {
  host = "unix:///var/run/docker.sock"
}

# Docker Network
resource "docker_network" "task_network" {
  name = "task-network"
}

# PostgreSQL Database
resource "docker_image" "postgres" {
  name = "postgres:15"
}

resource "docker_container" "postgres" {
  name  = "task-list-postgres"
  image = docker_image.postgres.image_id

  ports {
    internal = 5432
    external = 5432
  }

  env = [
    "POSTGRES_DB=demo_task_db",
    "POSTGRES_USER=demo-task",
    "POSTGRES_PASSWORD=demo-pwd"
  ]

  volumes {
    host_path      = "/tmp/postgres_data"
    container_path = "/var/lib/postgresql/data"
  }

  networks_advanced {
    name = docker_network.task_network.name
  }

  # Health check
  healthcheck {
    test         = ["CMD-SHELL", "pg_isready -U demo-task -d demo_task_db"]
    interval     = "10s"
    timeout      = "5s"
    retries      = 5
    start_period = "30s"
  }
}

# Task List Application
resource "docker_image" "task_app" {
  name = "task-list-app:latest"
  build {
    context    = ".."
    dockerfile = "Dockerfile"
  }
  depends_on = [docker_container.postgres]
}

resource "docker_container" "task_app" {
  name  = "task-list-app"
  image = docker_image.task_app.image_id

  ports {
    internal = 8080
    external = 8080
  }

  env = [
    "SPRING_DATASOURCE_URL=jdbc:postgresql://task-list-postgres:5432/demo_task_db",
    "SPRING_DATASOURCE_USERNAME=demo-task",
    "SPRING_DATASOURCE_PASSWORD=demo-pwd",
    "SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop",
    "SPRING_JPA_SHOW_SQL=true",
    "LOGGING_LEVEL_COM_TYGRUS_TASK_LIST=DEBUG",
    "SPRING_PROFILES_ACTIVE=docker",
    "DB_HOST=task-list-postgres",
    "DB_PORT=5432"
  ]

  volumes {
    host_path      = "${path.cwd}/../logs"
    container_path = "/app/logs"
  }

  networks_advanced {
    name = docker_network.task_network.name
  }

  depends_on = [docker_container.postgres]

  # Health check
  healthcheck {
    test         = ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
    interval     = "30s"
    timeout      = "3s"
    retries      = 3
    start_period = "40s"
  }
}

# pgAdmin (Optional Database Management Tool)
resource "docker_image" "pgadmin" {
  name = "dpage/pgadmin4:7"
}

resource "docker_container" "pgadmin" {
  name  = "task-list-pgadmin"
  image = docker_image.pgadmin.image_id

  ports {
    internal = 80
    external = 5050
  }

  env = [
    "PGADMIN_DEFAULT_EMAIL=admin@taskapp.com",
    "PGADMIN_DEFAULT_PASSWORD=admin123",
    "PGADMIN_CONFIG_SERVER_MODE=False"
  ]

  volumes {
    host_path      = "/tmp/pgadmin_data"
    container_path = "/var/lib/pgadmin"
  }

  networks_advanced {
    name = docker_network.task_network.name
  }

  depends_on = [docker_container.postgres]
}
