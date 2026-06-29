secret/
  └── my-company/                     # Root Team / Tenant Boundary
        ├── development/             # Environment Boundary
        │     ├── spring-boot-dual-database/
        │     │     ├── database     # { "username": "dev_user", "password": "..." }
        │     │     └── api-keys     # { "sendgrid": "SG.xxx" }
        │     └── order-service/
        │           └── database
        └── production/              # Strict Production Boundary
              ├── spring-boot-dual-database/
              │     ├── database
              │     └── api-keys
              └── order-service/
                    └── database
                    
                    
                    