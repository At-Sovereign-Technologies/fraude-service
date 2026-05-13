#!/usr/bin/env bash
# Quick smoke test for fraude-service
set -euo pipefail

BASE=${1:-http://localhost:8091/api/v1/fraude}

echo "=== Health ==="
curl -s "$BASE/health" | python3 -m json.tool

echo ""
echo "=== Metrics ==="
curl -s "$BASE/metrics" | python3 -m json.tool

echo ""
echo "=== Report event (M8-05 FAILED_AUTH_ATTEMPTS) ==="
curl -s -X POST "$BASE/eventos" \
  -H "Content-Type: application/json" \
  -d '{
    "source": "M8-05",
    "eventType": "FAILED_AUTH_ATTEMPTS",
    "originEventId": "evt-001",
    "verificationHash": "776e378fd564502a467b6027519270de7fe1f39c07c10bf695f5531d23963a34",
    "certifiedTimestamp": "2026-05-13T10:00",
    "logicalLocation": {
      "tableId": "MESA-001",
      "pollingStation": "PUESTO-015",
      "constituency": "ANTIOQUIA",
      "channel": "PRESENCIAL"
    },
    "metadata": {
      "failedAttempts": 7,
      "threshold": 5,
      "windowMinutes": 10
    }
  }' | python3 -m json.tool

echo ""
echo "=== List alerts ==="
curl -s "$BASE/alertas" | python3 -m json.tool

echo ""
echo "=== Report duplicate (same originEventId - should return existing) ==="
curl -s -X POST "$BASE/eventos" \
  -H "Content-Type: application/json" \
  -d '{
    "source": "M8-05",
    "eventType": "FAILED_AUTH_ATTEMPTS",
    "originEventId": "evt-001",
    "verificationHash": "776e378fd564502a467b6027519270de7fe1f39c07c10bf695f5531d23963a34",
    "certifiedTimestamp": "2026-05-13T10:00",
    "logicalLocation": {
      "tableId": "MESA-001",
      "channel": "PRESENCIAL"
    }
  }' | python3 -m json.tool

echo ""
echo "=== Report bad hash (should 400) ==="
curl -s -X POST "$BASE/eventos" \
  -H "Content-Type: application/json" \
  -d '{
    "source": "M8-05",
    "eventType": "FAILED_AUTH_ATTEMPTS",
    "originEventId": "evt-bad",
    "verificationHash": "0000000000000000000000000000000000000000000000000000000000000000",
    "certifiedTimestamp": "2026-05-13T10:00:00"
  }' | python3 -m json.tool

echo ""
echo "=== Done ==="
