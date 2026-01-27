# Linux Cloud Server Deployment Checklist

## Pre-Deployment Checklist

### Server Requirements
- [ ] Linux OS (Ubuntu 20.04+, CentOS 7+, Debian 10+ recommended)
- [ ] Minimum 2GB RAM, 4GB recommended
- [ ] Minimum 20GB disk space
- [ ] Root or sudo access

### Docker Installation
- [ ] Docker installed (version 20.10+)
- [ ] Docker Compose installed (version 2.0+)
- [ ] Docker service enabled and running
- [ ] User added to docker group (optional but recommended)

```bash
# Check Docker version
docker --version
docker-compose --version

# Check Docker status
systemctl status docker
```

### Network Configuration
- [ ] Firewall configured (ufw/iptables)
- [ ] Ports 80 and 8082 open (or custom ports)
- [ ] Ports 3306 and 6379 closed (should not be exposed externally)

```bash
# Check open ports
sudo ufw status
# or
sudo netstat -tulpn | grep LISTEN
```

### File Permissions
- [ ] Write permissions for application directory
- [ ] Sufficient disk space for Docker volumes
- [ ] Sufficient disk space for Docker images

```bash
# Check disk space
df -h

# Check directory permissions
ls -ld /path/to/library_management_system
```

## Deployment Steps

### 1. Application Setup
- [ ] Clone repository
- [ ] Copy .env.example to .env
- [ ] Update all passwords in .env file
- [ ] Generate secure JWT secret

### 2. Docker Configuration
- [ ] Review docker-compose.prod.yml
- [ ] Verify port mappings (avoid conflicts)
- [ ] Verify resource limits (adjust if needed)
- [ ] Verify volume paths exist

### 3. Initial Deployment
- [ ] Build images: `docker-compose -f docker-compose.prod.yml build`
- [ ] Start services: `docker-compose -f docker-compose.prod.yml up -d`
- [ ] Check service status: `docker-compose -f docker-compose.prod.yml ps`
- [ ] Check health status: Verify all services show "healthy"

### 4. Application Verification
- [ ] Access frontend: http://server-ip:82
- [ ] Access backend: http://server-ip:8082/actuator/health
- [ ] Access API docs: http://server-ip:8082/swagger-ui.html
- [ ] Test login with admin credentials
- [ ] Test basic operations (list books, borrow, etc.)

## Post-Deployment Checklist

### Security
- [ ] Change default admin password
- [ ] Change default user password
- [ ] Verify strong passwords in .env
- [ ] Configure reverse proxy with HTTPS (nginx/caddy)
- [ ] Close unnecessary ports
- [ ] Set up fail2ban (optional but recommended)
- [ ] Review .env file - ensure no hardcoded credentials in code

### Monitoring
- [ ] Set up log monitoring
- [ ] Set up disk space monitoring
- [ ] Set up resource monitoring (CPU, RAM, disk)
- [ ] Configure alerts for service failures
- [ ] Test backup procedures

### Backup Strategy
- [ ] Configure automated database backups
- [ ] Test backup restore procedures
- [ ] Backup .env file (securely)
- [ ] Document backup locations

### Performance
- [ ] Monitor resource usage during normal operations
- [ ] Monitor resource usage under load
- [ ] Adjust resource limits in docker-compose.prod.yml if needed
- [ ] Enable MySQL slow query log (if performance issues)
- [ ] Review and optimize database queries

## Common Issues and Solutions

### Issue: Services won't start
**Symptoms:** `docker-compose ps` shows services as "Exit" or "Restarting"

**Solutions:**
1. Check logs: `docker-compose logs <service-name>`
2. Verify .env file exists and has correct format
3. Check port conflicts: `netstat -tulpn | grep <port>`
4. Verify sufficient disk space: `df -h`

### Issue: Database connection errors
**Symptoms:** Backend logs show "Unable to connect to database"

**Solutions:**
1. Check MySQL health: `docker inspect library-mysql-prod | grep -A 10 Health`
2. Verify MYSQL_ROOT_PASSWORD matches in .env
3. Check MySQL logs: `docker-compose logs mysql`
4. Wait for MySQL to fully start (may take 30-60 seconds on first run)

### Issue: Redis connection errors
**Symptoms:** Backend logs show "Unable to connect to Redis"

**Solutions:**
1. Verify REDIS_PASSWORD matches in .env
2. Check Redis health: `docker inspect library-redis-prod | grep -A 10 Health`
3. Check Redis logs: `docker-compose logs redis`

### Issue: Out of memory errors
**Symptoms:** Services killed, OOM errors in logs

**Solutions:**
1. Check memory usage: `docker stats`
2. Increase server RAM or reduce memory limits in docker-compose.prod.yml
3. Reduce number of running containers
4. Enable swap space

### Issue: Permission denied errors
**Symptoms:** Docker can't write to volumes

**Solutions:**
1. Check directory permissions: `ls -la <volume-path>`
2. Fix permissions: `sudo chown -R $USER:$USER <volume-path>`
3. Run Docker with sudo (if user not in docker group)

## Maintenance Tasks

### Daily
- [ ] Check service health status
- [ ] Review error logs for critical issues
- [ ] Monitor disk space usage

### Weekly
- [ ] Review access logs
- [ ] Check for Docker image updates
- [ ] Verify backup integrity

### Monthly
- [ ] Rotate logs if needed
- [ ] Review and update security patches
- [ ] Test backup restore procedures
- [ ] Update Docker images: `docker-compose pull && docker-compose up -d`

### As Needed
- [ ] Update application code
- [ ] Scale services (if needed)
- [ ] Adjust resource limits
- [ ] Update security configurations

## Rollback Procedure

If deployment fails or causes issues:

```bash
# Stop all services
docker-compose -f docker-compose.prod.yml down

# Restore previous version (if backed up)
docker-compose -f docker-compose.prod.yml up -d

# If data corruption, restore from backup
docker-compose -f docker-compose.prod.yml exec -T mysql mysql -u root -p library_management < backup.sql
```

## Performance Tuning

### MySQL Optimization
For high-traffic deployments, add custom MySQL config:

```yaml
# In docker-compose.prod.yml, add to mysql service:
volumes:
  - mysql-data:/var/lib/mysql
  - ./backend/src/main/resources/schema.sql:/docker-entrypoint-initdb.d/01-schema.sql
  - ./backend/init_users.sql:/docker-entrypoint-initdb.d/02-init-users.sql
  - ./mysql-custom.cnf:/etc/mysql/conf.d/custom.cnf:ro
```

Create `mysql-custom.cnf`:
```ini
[mysqld]
max_connections = 200
innodb_buffer_pool_size = 512M
innodb_log_file_size = 128M
```

### Application Optimization
- Enable connection pooling (already configured in Spring Boot)
- Add caching headers to nginx.conf
- Enable gzip compression in nginx.conf
- Consider CDN for static assets

## Contact Support

If issues persist:
1. Check logs: `docker-compose -f docker-compose.prod.yml logs -f`
2. Review this checklist
3. Consult DEPLOYMENT_LINUX.md for detailed procedures
4. Check AGENTS.md for development patterns
