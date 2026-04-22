resource "github_repository" "spring_app" {
  name = "TUES-2026-PBL-11-klas/The-four-horsemen-of-coding-DaskaTraz"
  description = "Java + Spring Boot application"
  visibility  = "private"

  has_issues   = true
  has_projects = false
  has_wiki     = false

  auto_init          = true
  gitignore_template = "Java"
  license_template   = "mit"
}
