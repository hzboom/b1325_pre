#!/bin/bash
NAMESPACE="${1:-codebase_b1352_app}"
docker build -t "$NAMESPACE" .