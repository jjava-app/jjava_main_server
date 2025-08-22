#!/usr/bin/env sh
set -e

# 옵션들을 포지셔널 파라미터($@)로 설정 (문자열로 합치지 않음)
set -- \
  --appendonly "yes" \
  --appendfsync "everysec" \
  --save "900" "1" \
  --save "300" "10" \
  --save "60" "10000" \
  --dir "/data"

# 비밀번호가 있으면 requirepass 추가 (값은 반드시 따옴표)
if [ -n "${REDIS_PASSWORD:-}" ]; then
  echo "[redis-entrypoint] Using password (len: ${#REDIS_PASSWORD})"
  set -- "$@" --requirepass "$REDIS_PASSWORD"
else
  echo "[redis-entrypoint] No password"
fi

# 개별 인자로 정확히 전달하여 실행
exec redis-server "$@"