--成功返回1,失败返回0
if (redis.call('exists', KEYS[1]) == 1)
then
    local cc = redis.call('get', KEYS[1]);
    local stock = tonumber(cc);
    local num = tonumber(ARGV[1]);
    if (stock < num) then
        return -1;
    end ;
    if (stock >= num) then
          redis.call('decrBy', KEYS[1], num);
          redis.call('incrBy', KEYS[2], num);
          return 1;
    end ;
end ;
return -1;
