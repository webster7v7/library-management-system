# Production Deployment Guide for Linux Cloud Server

## Prerequisites

- Linux server with Docker and Docker Compose installed
- Minimum 2GB RAM (4GB recommended)
- 20GB disk space
- Open ports: 80, 8082 (or custom ports)

## Quick Start

### 1. Clone and Prepare

```bash
git clone <repository-url>
cd library_management_system
cp .env.example .env
```

### 2. Configure Environment Variables

Edit `.env` file and set strong passwords:

```bash
# Generate secure passwords (optional)
openssl rand -base64 32  # For database
openssl rand -base64 32  # For Redis
openssl rand -base64 64  # For JWT secret
```

Update `.env` with generated values.

### 3. Build and Start Services

```bash
# Build and start all services
docker compose -f docker-compose.prod.yml up -d --build

# Check service status
docker compose -f docker-compose.prod.yml ps

# View logs
docker compose -f docker-compose.prod.yml logs -f   
```

### 4. Access Application

- Frontend: http://your-server-ip:82
- Backend API: http://your-server-ip:8082
- API Documentation: http://your-server-ip:8082/swagger-ui.html

**Default Credentials:**
- Admin: `admin / admin`
- User: `user / user`

⚠️ **IMPORTANT:** Change default passwords after first login!

## Service Management

```bash
# Stop services
docker-compose -f docker-compose.prod.yml down

# Stop and remove volumes (WARNING: deletes all data)
docker-compose -f docker-compose.prod.yml down -v

# Restart services
docker-compose -f docker-compose.prod.yml restart

# Update specific service
docker-compose -f docker-compose.prod.yml up -d --build backend
```

## Monitoring

```bash
# Check service health
docker inspect --format='{{.State.Health.Status}}' library-mysql-prod
docker inspect --format='{{.State.Health.Status}}' library-redis-prod
docker inspect --format='{{.State.Health.Status}}' library-backend-prod
docker inspect --format='{{.State.Health.Status}}' library-frontend-prod

# View resource usage
docker stats

# View logs for specific service
docker-compose -f docker-compose.prod.yml logs backend
docker-compose -f docker-compose.prod.yml logs mysql
```

## Backup

### Database Backup

```bash
# Backup MySQL
docker-compose -f docker-compose.prod.yml exec mysql mysqldump -u root -p library_management > backup_$(date +%Y%m%d).sql

# Restore MySQL
docker-compose -f docker-compose.prod.yml exec -T mysql mysql -u root -p library_management < backup_20250127.sql
```

### Volume Backup

```bash
# Backup all volumes
docker run --rm -v library_management_mysql-data:/data -v $(pwd):/backup ubuntu tar czf /backup/mysql-backup.tar.gz /data
docker run --rm -v library_management_redis-data:/data -v $(pwd):/backup ubuntu tar czf /backup/redis-backup.tar.gz /data
```

## Troubleshooting

### Services Won't Start

```bash
# Check logs
docker-compose -f docker-compose.prod.yml logs

# Check port conflicts
netstat -tulpn | grep -E ':(80|8082|3306|6379)'

# Rebuild from scratch
docker-compose -f docker-compose.prod.yml down -v
docker-compose -f docker-compose.prod.yml up -d --build
```

### Database Connection Issues

```bash
# Verify MySQL is ready
docker-compose -f docker-compose.prod.yml exec mysql mysqladmin ping -u root -p

# Check MySQL logs
docker-compose -f docker-compose.prod.yml logs mysql

# Test connection from backend
docker-compose -f docker-compose.prod.yml exec backend wget -O- http://mysql:3306
```

### Redis Connection Issues

```bash
# Test Redis
docker-compose -f docker-compose.prod.yml exec redis redis-cli -a $REDIS_PASSWORD ping

# Check Redis logs
docker-compose -f docker-compose.prod.yml logs redis
```

## Security Recommendations

1. **Change all default passwords** before production deployment
2. **Use HTTPS** - configure reverse proxy (Nginx/Caddy) with SSL certificates
3. **Close unused ports** via firewall (ufw/iptables)
4. **Regular updates** - keep Docker images updated
5. **Monitor logs** - set up log aggregation (ELK, Loki, etc.)
6. **Enable fail2ban** - protect against brute force attacks
7. **Use strong JWT secret** - minimum 64 characters, randomly generated
8. **Regular backups** - automate database backups
9. **Restrict access** - use firewall rules to limit who can access admin endpoints

## Scaling

For higher load, consider:

1. **Use external MySQL/Redis** instead of containers
2. **Add load balancer** (Nginx, HAProxy) for multiple backend instances
3. **Add CDN** for static frontend assets
4. **Enable caching** (Redis for session, database query cache)
5. **Optimize database** (add indexes, tune MySQL config)

## Support

For issues:
- Check logs: `docker-compose -f docker-compose.prod.yml logs`
- Review health status: `docker-compose -f docker-compose.prod.yml ps`
- Consult project AGENTS.md for development patterns
