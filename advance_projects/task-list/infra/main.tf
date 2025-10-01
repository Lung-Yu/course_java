# ==========================================
# Task List 應用程式 - 生產部署環境
# 部署方式: terraform apply
# ==========================================

terraform {
  required_version = ">= 1.0"
  
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }
  
  # 狀態管理（生產環境建議使用遠端後端）
  # backend "s3" {
  #   bucket = "task-list-terraform-state"
  #   key    = "prod/terraform.tfstate"
  #   region = "us-east-1"
  # }
}

provider "docker" {
  host = "unix:///var/run/docker.sock"
}

# ==========================================
# 變數定義
# ==========================================

variable "environment" {
  description = "部署環境"
  type        = string
  default     = "production"
}

variable "db_name" {
  description = "資料庫名稱"
  type        = string
  default     = "demo_task_db"
}

variable "db_user" {
  description = "資料庫使用者"
  type        = string
  default     = "demo-task"
  sensitive   = true
}

variable "db_password" {
  description = "資料庫密碼"
  type        = string
  default     = "demo-pwd"
  sensitive   = true
}

variable "app_port" {
  description = "應用程式端口"
  type        = number
  default     = 8080
}

variable "db_port" {
  description = "資料庫端口"
  type        = number
  default     = 5432
}

variable "enable_pgadmin" {
  description = "是否啟用 pgAdmin"
  type        = bool
  default     = false
}

# ==========================================
# Docker 網路
# ==========================================

resource "docker_network" "task_network" {
  name   = "task-list-prod-network"
  driver = "bridge"
  
  ipam_config {
    subnet  = "172.20.0.0/16"
    gateway = "172.20.0.1"
  }
  
  labels {
    label = "environment"
    value = var.environment
  }
}

# ==========================================
# PostgreSQL 資料庫
# ==========================================

resource "docker_volume" "postgres_data" {
  name = "task-list-postgres-prod-data"
  
  labels {
    label = "environment"
    value = var.environment
  }
}

resource "docker_image" "postgres" {
  name         = "postgres:15-alpine"
  keep_locally = true
}

resource "docker_container" "postgres" {
  name  = "task-list-postgres-prod"
  image = docker_image.postgres.image_id
  
  restart = "unless-stopped"

  ports {
    internal = var.db_port
    external = var.db_port
  }

  env = [
    "POSTGRES_DB=${var.db_name}",
    "POSTGRES_USER=${var.db_user}",
    "POSTGRES_PASSWORD=${var.db_password}",
    "POSTGRES_INITDB_ARGS=--encoding=UTF8 --locale=C",
    "PGDATA=/var/lib/postgresql/data/pgdata"
  ]

  volumes {
    volume_name    = docker_volume.postgres_data.name
    container_path = "/var/lib/postgresql/data"
  }

  networks_advanced {
    name = docker_network.task_network.name
  }

  healthcheck {
    test         = ["CMD-SHELL", "pg_isready -U ${var.db_user} -d ${var.db_name}"]
    interval     = "10s"
    timeout      = "5s"
    retries      = 5
    start_period = "30s"
  }
  
  labels {
    label = "environment"
    value = var.environment
  }
  
  labels {
    label = "service"
    value = "database"
  }
}

# ==========================================
# Task List 應用程式
# ==========================================

resource "docker_image" "task_app" {
  name = "task-list-app:prod"
  
  build {
    context    = "${path.cwd}/.."
    dockerfile = "Dockerfile"
    
    build_args = {
      BUILD_ENV = "production"
    }
    
    label = {
      environment = var.environment
      version     = "1.0.0"
    }
  }
  
  keep_locally = false
}

resource "docker_volume" "app_logs" {
  name = "task-list-app-prod-logs"
  
  labels {
    label = "environment"
    value = var.environment
  }
}

resource "docker_volume" "app_uploads" {
  name = "task-list-app-prod-uploads"
  
  labels {
    label = "environment"
    value = var.environment
  }
}

resource "docker_container" "task_app" {
  name  = "task-list-app-prod"
  image = docker_image.task_app.image_id
  
  restart = "unless-stopped"

  ports {
    internal = var.app_port
    external = var.app_port
  }

  env = [
    "SPRING_DATASOURCE_URL=jdbc:postgresql://${docker_container.postgres.name}:${var.db_port}/${var.db_name}",
    "SPRING_DATASOURCE_USERNAME=${var.db_user}",
    "SPRING_DATASOURCE_PASSWORD=${var.db_password}",
    "SPRING_JPA_HIBERNATE_DDL_AUTO=validate",
    "SPRING_JPA_SHOW_SQL=false",
    "LOGGING_LEVEL_COM_TYGRUS_TASK_LIST=INFO",
    "LOGGING_LEVEL_ORG_SPRINGFRAMEWORK=WARN",
    "SPRING_PROFILES_ACTIVE=prod",
    "SERVER_PORT=${var.app_port}",
    "DB_HOST=${docker_container.postgres.name}",
    "DB_PORT=${var.db_port}"
  ]

  volumes {
    volume_name    = docker_volume.app_logs.name
    container_path = "/app/logs"
  }
  
  volumes {
    volume_name    = docker_volume.app_uploads.name
    container_path = "/app/uploads"
  }

  networks_advanced {
    name = docker_network.task_network.name
  }

  depends_on = [docker_container.postgres]

  healthcheck {
    test         = ["CMD", "curl", "-f", "http://localhost:${var.app_port}/actuator/health"]
    interval     = "30s"
    timeout      = "10s"
    retries      = 3
    start_period = "60s"
  }
  
  labels {
    label = "environment"
    value = var.environment
  }
  
  labels {
    label = "service"
    value = "application"
  }
}

# ==========================================
# pgAdmin (可選)
# ==========================================

resource "docker_image" "pgadmin" {
  count        = var.enable_pgadmin ? 1 : 0
  name         = "dpage/pgadmin4:8"
  keep_locally = true
}

resource "docker_volume" "pgadmin_data" {
  count = var.enable_pgadmin ? 1 : 0
  name  = "task-list-pgadmin-prod-data"
  
  labels {
    label = "environment"
    value = var.environment
  }
}

resource "docker_container" "pgadmin" {
  count = var.enable_pgadmin ? 1 : 0
  name  = "task-list-pgadmin-prod"
  image = docker_image.pgadmin[0].image_id
  
  restart = "unless-stopped"

  ports {
    internal = 80
    external = 5050
  }

  env = [
    "PGADMIN_DEFAULT_EMAIL=admin@taskapp.com",
    "PGADMIN_DEFAULT_PASSWORD=admin123",
    "PGADMIN_CONFIG_SERVER_MODE=False",
    "PGADMIN_LISTEN_PORT=80"
  ]

  volumes {
    volume_name    = docker_volume.pgadmin_data[0].name
    container_path = "/var/lib/pgadmin"
  }

  networks_advanced {
    name = docker_network.task_network.name
  }

  depends_on = [docker_container.postgres]
  
  labels {
    label = "environment"
    value = var.environment
  }
  
  labels {
    label = "service"
    value = "admin-tool"
  }
}

# ==========================================
# 輸出
# ==========================================

output "application_url" {
  description = "應用程式訪問地址"
  value       = "http://localhost:${var.app_port}"
}

output "database_connection" {
  description = "資料庫連接信息"
  value = {
    host     = "localhost"
    port     = var.db_port
    database = var.db_name
    user     = var.db_user
  }
  sensitive = true
}

output "pgadmin_url" {
  description = "pgAdmin 訪問地址"
  value       = var.enable_pgadmin ? "http://localhost:5050" : "pgAdmin 未啟用"
}

output "container_names" {
  description = "容器名稱"
  value = {
    database    = docker_container.postgres.name
    application = docker_container.task_app.name
    pgadmin     = var.enable_pgadmin ? docker_container.pgadmin[0].name : "未啟用"
  }
}

output "deployment_info" {
  description = "部署信息"
  value = {
    environment = var.environment
    network     = docker_network.task_network.name
    timestamp   = timestamp()
  }
}
