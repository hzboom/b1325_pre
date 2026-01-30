FROM eclipse-temurin:17-jdk

# Install X11 libraries for GUI support
RUN apt-get update && apt-get install -y \
    libxtst6 \
    libxrender1 \
    libxi6 \
    libxrandr2 \
    libxcursor1 \
    libxinerama1 \
    libxft2 \
    x11-apps \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace

# Copy the source code
COPY . .

# Set environment for X11 forwarding
ENV DISPLAY=:0

# Default to bash shell for development
CMD ["/bin/bash"]