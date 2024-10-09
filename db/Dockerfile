# Use the latest MySQL image
FROM mysql
# Set the working directory
WORKDIR /tmp
# Copy world.sql to the working directory of the container
COPY world.sql /tmp/
# Copy the world.sql file to docker-entrypoint-initdb.d.
# Scripts and "world.sql" in this folder are executed on container startup.
# This is specific to MySQL.
COPY world.sql /docker-entrypoint-initdb.d
# Set the root password as group-5
ENV MYSQL_ROOT_PASSWORD group-5