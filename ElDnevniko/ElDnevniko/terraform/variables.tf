variable "github_owner" {
  description = "GitHub username or org"
  type        = string
}

variable "app_name" {
  description = "Name for the repository"
  type        = string
  default     = "my-spring-app"
}
