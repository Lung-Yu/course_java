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


resource "docker_image" "postgres" {
  name = "postgres:15"
}

resource "docker_container" "postgres" {
  name  = "demo-task-postgres"
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
}
