local result={}
local index=0
repeat
  local temp = redis.call('SCAN',index,'MATCH',KEYS[1] .. '*')
  index=tonumber(temp[1])
  for i=1,#temp[2] do
    table.insert(result,temp[2][i])
  end
until index == 0
return result