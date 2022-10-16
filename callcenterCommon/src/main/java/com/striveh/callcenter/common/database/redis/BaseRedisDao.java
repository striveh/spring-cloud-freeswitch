package com.striveh.callcenter.common.database.redis;

import com.striveh.callcenter.common.util.JsonTool;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * https://www.awebide.com/apis/aweb-redis-client/doc/cn/com/agree/aweb/redis/client/jedis/ShardedJedis.html
 */
public class BaseRedisDao {
	/** 日志 */
	protected Logger logger = LogManager.getLogger(this.getClass());
	/** redis连接池 */
	protected ShardedJedisPool shardedJedisPool;


    public  boolean tryLock(String key, String value) throws Exception{
        ShardedJedis jedis=null;
        try {
            jedis = shardedJedisPool.getResource();
            String result = jedis.set(key, value, "NX", "EX", 5);
            if (StringUtils.equals("OK", result)) {
                logger.info(key+"获取redis锁成功");
                return true;
            } else {
                for (int i = 1; i < 6; i++) {
                    logger.info(key+"第"+i+"次尝试获取redis锁");
                    Thread.sleep(200);
                    String tryResult = jedis.set(key, value, "NX", "EX", 5);
                    if (StringUtils.equals("OK", tryResult)) {
                        logger.info(key+"第"+i+"次尝试获取redis锁成功");
                        return true;
                    }
                }
            }
            logger.info(key+"获取redis锁失败");
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }

	/**
	 * 查询对象是否存
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.exists(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 保存或更新对象
	 * 
	 * @param key
	 * @param obj
	 * @return
	 */
	public long saveOrUpdate(String key, Object obj) {
		return saveOrUpdate(key, obj, 0);
	}

	/**
	 * 设置对象过期时间
	 * 
	 * @param key
	 * @param expSecond
	 * @return 单位秒
	 */
	public long expire(String key, int expSecond) {
		return saveOrUpdate(key, null, expSecond);
	}

	/**
	 * 保存或更新对象,同时更新对象的失效时间,expSecond>0时才设置失效时间
	 * 
	 * @param key
	 * @param obj
	 * @param expSecond 单位秒
	 * @return
	 */
	public long saveOrUpdate(String key, Object obj, int expSecond) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			long result = 0;
			if (obj != null) {
				jedis.set(key, JsonTool.getString(obj));
				result = 1;
			}
			if (expSecond > 0) {
				result = jedis.expire(key, expSecond);
			}
			return result;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 保存或更新对象,同时更新对象的失效时间,expSecond>0时才设置失效时间
	 *
	 * @param key
	 * @param obj
	 * @param expSecond 单位秒
	 * @return
	 */
	public long saveOrUpdateByte(String key, Object obj, int expSecond) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			long result = 0;
			if (obj != null) {
				jedis.set(key.getBytes(), serialize(obj));
				result = 1;
			}
			if (expSecond > 0) {
				result = jedis.expire(key, expSecond);
			}
			return result;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 根据key取得对象,同时更新对象的失效时间,expSecond>0时才设置失效时间
	 *
	 * @param key
	 * @param cls
	 * @param expSecond 单位秒
	 * @return
	 */
	public <T> T getWithByte(String key, Class<T> cls, int expSecond) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			byte[] bytes = jedis.get(key.getBytes());
			if (bytes != null) {
				if (expSecond > 0) {
					jedis.expire(key.getBytes(), expSecond);
				}
				return (T)unserizlize(bytes);
			}
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	//序列化
	public static byte [] serialize(Object obj){
		ObjectOutputStream obi=null;
		ByteArrayOutputStream bai=null;
		try {
			bai=new ByteArrayOutputStream();
			obi=new ObjectOutputStream(bai);
			obi.writeObject(obj);
			byte[] byt=bai.toByteArray();
			return byt;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	//反序列化
	public static Object unserizlize(byte[] byt){
		ObjectInputStream oii=null;
		ByteArrayInputStream bis=null;
		bis=new ByteArrayInputStream(byt);
		try {
			oii=new ObjectInputStream(bis);
			Object obj=oii.readObject();
			return obj;
		} catch (Exception e) {

			e.printStackTrace();
		}


		return null;
	}

	/**
	 * 删除对象:对象不存在，也返回true
	 * 
	 * @param key
	 * @return
	 */
	public long delete(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.del(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 根据key取得对象
	 * 
	 * @param key
	 * @param cls
	 * @return
	 */
	public <T> T get(String key, Class<T> cls) {
		return get(key, cls, 0);
	}

	/**
	 * 根据key取得对象,同时更新对象的失效时间,expSecond>0时才设置失效时间
	 * 
	 * @param key
	 * @param cls
	 * @param expSecond 单位秒
	 * @return
	 */
	public <T> T get(String key, Class<T> cls, int expSecond) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			String str = jedis.get(key);
			if (str != null) {
				if (expSecond > 0) {
					jedis.expire(key, expSecond);
				}
				return JsonTool.getObj(str, cls);
			}
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 操作HashMap：保存或更新值
	 * 
	 * @param storekey
	 * @param mapKey
	 * @param obj
	 * @return
	 */
	public long saveOrUpdateMapVal(String storekey, String mapKey, Object obj) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.hset(storekey, mapKey, JsonTool.getString(obj));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 操作HashMap：取得值
	 * 
	 * @param storekey
	 * @param mapKey
	 * @param cls
	 * @return
	 */
	public <T> T getMapVal(String storekey, String mapKey, Class<T> cls) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			String str = jedis.hget(storekey, mapKey);
			if (str != null) {
				return JsonTool.getObj(str, cls);
			}
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：向有序集合中添加元素
	 * @param key 要操作的key
	 * @param score 保存时使用的分值
	 * @param member 要保存的元素
	 * @param expSecond key的过期时间
	 * @throws
	 * @return            
	 */
	public long zadd(String key, double score, String member, int expSecond) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			if (expSecond > 0) {
				jedis.expire(key, expSecond);
			}
			return jedis.zadd(key, score, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：向有序集合中添加元素
	 * @param key 要操作的key
	 * @param scoreMembers 要保存的所有元素，Map的key保存元素的值，Map的value保存分值
	 * @param expSecond key的过期时间
	 * @throws
	 * @return            
	 */
	public Long zadd(String key, Map<String, Double> scoreMembers, int expSecond) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			if (expSecond > 0) {
				jedis.expire(key, expSecond);
			}
			return jedis.zadd(key, scoreMembers);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：返回有序集合元素的个数
	 * @param key 要操作的key
	 * @throws
	 * @return            
	 */
	public Long zcard(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zcard(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	/**
	 * 无序集合操作：随机返回集合 key 中指定数量的成员
	 * @param key 要操作的key
	 * @throws
	 * @return
	 */
	public List<String> smembers(String key,Integer count) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.srandmember(key,count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	/**
	 * 无序集合操作：向无序集合中添加元素
	 * @param key 要操作的key
	 * @param expSecond key的过期时间
	 * @throws
	 * @return
	 */
	public Long unorderedzadd(String key,Object obj,int expSecond) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			if (expSecond > 0) {
				jedis.expire(key, expSecond);
			}
			return jedis.sadd(key,JsonTool.getJsonString(obj));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 返回成员 member 是否是存储的集合 key的成员.
	 * @param key 要操作的key
	 * @throws
	 * @return
	 */
	public Boolean sismember(String key,Object obj) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.sismember(key,JsonTool.getJsonString(obj));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 无序集合操作：移除指定的元素.
	 * @param key 要操作的key
	 * @throws
	 * @return
	 */
	public Long unorderedzrem(String key,Object obj) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.srem(key,JsonTool.getJsonString(obj));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 无序集合操作：返回有无序集合元素的个数
	 * @param key 要操作的key
	 * @throws
	 * @return
	 */
	public Long unorderedZcard(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.scard(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 获取无序集合值
	 * @param <T>
	 * @return
	 */
	public <T> Set<T> getSet(String key,Class<T> clazz){
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			Set<String> smembers = jedis.smembers(key);
			if(smembers!=null){
				Set<T> collect = smembers.stream().map(e -> JsonTool.getObj(e, clazz)).collect(Collectors.toSet());
				return collect;
			}
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	/**
	 * 有序集合操作：返回有序集合分值介于min和max（包含min和max）之间的元素的个数 
	 * @param key 要操作的key
	 * @param min score查询起始值
	 * @param max score查询结束值
	 * @throws
	 * @return            
	 */
	public Long zcount(String key, double min, double max) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zcount(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：返回有序集合分值介于min和max（包含min和max）之间的元素的个数 
	 * @param key 要操作的key
	 * @param min score查询起始值
	 * @param max score查询结束值
	 * @throws
	 * @return            
	 */
	public Long zcount(String key, String min, String max) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zcount(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：有序集合递增排序后返回索引从start到end之间的所有元素（包含start和end）
	 * @param key 要操作的key
	 * @param start 开始位置，不能为负数，为附属时方法返回[]
	 * @param end 结束位置
	 * @throws
	 * @return            
	 */
	public Set<String> zrange(String key, long start, long end) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zrange(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：元素按照score递增排序后返回所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的元素。
	 * @param key 要操作的key
	 * @param min score查询起始值
	 * @param max score查询结束值
	 * @throws
	 * @return            
	 */
	public Set<String> zrangeByScore(String key, double min, double max) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zrangeByScore(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：元素按照score递增排序后返回所有 score 值介于 min 和 max 之间(包括等于 min 或 max )大于offset的count个元素。 
	 * @param key 要操作的key
	 * @param min score查询起始值
	 * @param max score查询结束值
	 * @param offset 限制的score值
	 * @param count 查询的元素的数量
	 * @throws
	 * @return            
	 */
	public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zrangeByScore(key, min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：元素按照score递增排序后返回所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的元素。
	 * @param key 要操作的key
	 * @param min score查询起始值
	 * @param max score查询结束值
	 * @throws
	 * @return            
	 */
	public Set<String> zrangeByScore(String key, String max, String min) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zrangeByScore(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：元素按照score递增排序后返回所有 score 值介于 min 和 max 之间(包括等于 min 或 max )大于offset的count个元素。 
	 * @param key 要操作的key
	 * @param min score查询起始值
	 * @param max score查询结束值
	 * @param offset 限制的score值
	 * @param count 查询的元素的数量
	 * @throws
	 * @return            
	 */
	public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zrangeByScore(key, min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：有序集合操作：有序集合递减排序后返回start到end之间的所有元素（包含start和end）
	 * @param key 要操作的key
	 * @param start 开始位置，不能为负数，为附属时方法返回[]
	 * @param end 结束位置
	 * @throws
	 * @return            
	 */
	public Set<String> zrevrange(String key, long start, long end) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zrevrange(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：元素按照score递减排序后返回所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的元素。
	 * @param key 要操作的key
	 * @param min score查询起始值
	 * @param max score查询结束值
	 * @throws
	 * @return            
	 */
	public Set<String> zrevrangeByScore(String key, double min, double max) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zrevrangeByScore(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：元素按照score递减排序后返回所有 score 值介于 min 和 max 之间(包括等于 min 或 max )大于offset的count个元素。 
	 * @param key 要操作的key
	 * @param min score查询起始值
	 * @param max score查询结束值
	 * @param offset 限制的score值
	 * @param count 查询的元素的数量
	 * @throws
	 * @return            
	 */
	public Set<String> zrevrangeByScore(String key, double min, double max, int offset, int count) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zrevrangeByScore(key, min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：元素按照score递减排序后返回所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的元素。
	 * @param key 要操作的key
	 * @param min score查询起始值
	 * @param max score查询结束值
	 * @throws
	 * @return            
	 */
	public Set<String> zrevrangeByScore(String key, String max, String min) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zrevrangeByScore(key, min, max);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：元素按照score递减排序后返回所有 score 值介于 min 和 max 之间(包括等于 min 或 max )大于offset的count个元素。 
	 * @param key 要操作的key
	 * @param min score查询起始值
	 * @param max score查询结束值
	 * @param offset 限制的score值
	 * @param count 查询的元素的数量
	 * @throws
	 * @return            
	 */
	public Set<String> zrevrangeByScore(String key, String min, String max, int offset, int count) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zrevrangeByScore(key, min, max, offset, count);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：返回指定的key递增排序后元素member的索引，索引从0开始
	 * @param key 要操作的key
	 * @param member 元素
	 * @throws
	 * @return            
	 */
	public Long zrank(String key, String member) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zrank(key, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：返回指定的key递减排序后元素member的索引，索引从0开始
	 * @param key 要操作的key
	 * @param member 元素
	 * @throws
	 * @return            
	 */
	public Long zrevrank(String key, String member) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zrevrank(key, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：从有序集合中移除所有的members元素
	 * @param key  要操作的key
	 * @param members 要移除元素的数组
	 * @throws
	 * @return            
	 */
	public Long zrem(String key, String... members) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zrem(key, members);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：递减排序后从有序集合中移除索引从start开始到end(包含start和end)的所有的元素
	 * @param key 要操作的key
	 * @param start 索引起始位置
	 * @param end 索引结束位置
	 * @throws
	 * @return            
	 */
	public Long zremrangeByRank(String key, long start, long end) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zremrangeByRank(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：递减排序后从有序集合中移除分值从start开始到end(包含start和end)的所有的元素
	 * @param key 要操作的key
	 * @param start 分值起始位置
	 * @param end 分值结束位置
	 * @throws
	 * @return            
	 */
	public Long zremrangeByScore(String key, double start, double end) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zremrangeByScore(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 有序集合操作：递减排序后从有序集合中移除分值从start开始到end(包含start和end)的所有的元素
	 * @param key 要操作的key
	 * @param start 分值起始位置
	 * @param end 分值结束位置
	 * @throws
	 * @return            
	 */
	public Long zremrangeByScore(String key, String start, String end) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.zremrangeByScore(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * HASH操作：查看指定key中的field是否存在
	 * @param key 指定的key
	 * @param field 指定查询的键对应的属性
	 * @return
	 */
	public boolean hexists(String key, String field) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.hexists(key, field);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * HASH操作：获取指定key中的field的值
	 * @param key 指定的key
	 * @param field 指定查询的键对应的属性
	 * @return
	 */
	public String hget(String key, String field) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.hget(key, field);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	
	/**
	 * HASH操作：向指定key中添加值
	 * @param key 指定的key
	 * @param field 指定查询的键对应的属性
	 * @return
	 */
	public Long	hset(String key, String field, String value) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.hset(key, field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 队列操作：从队列右端添加元素
	 * @param key 待添加的key
	 * @param expSecond 过期时间
	 * @param members 待添加的元素
	 * @return
	 */
	public Long rpush(String key, int expSecond, String... members) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.rpush(key, members);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 队列操作：从队列中返回索引从从start到end(包含start和end)的所有的元素
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public List<String> lrange(String key, long start, long end) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.lrange(key, start, end);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 队列处理:从左将对象压入队列中
	 * 
	 * @param key
	 * @param obj
	 * @return
	 */
	public long leftPush(String key, Object obj) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.lpush(key, JsonTool.getString(obj));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 队列处理:从右取出对象
	 * 
	 * @param key
	 * @param
	 * @return
	 */
	public <T> T rightPop(String key, Class<T> cls) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			String str = jedis.rpop(key);
			if (str != null) {
				return JsonTool.getObj(str, cls);
			}
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 队列处理:从右取出对象(阻塞)
	 *
	 * @param key
	 * @param cls
	 * @return
	 */
	public <T> T bRightPop(String key, Class<T> cls) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			List<String> list = jedis.brpop(key);
			if (list != null && list.size() > 0) {
				return JsonTool.getObj(list.get(0), cls);
			}
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 队列操作：返回队列元素的个数
	 * @param key 要操作的key
	 * @throws
	 * @return
	 */
	public Long llen(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.llen(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 取得jedis对象
	 * 
	 * @return
	 */
	public ShardedJedis getShardedJedis() {
		return shardedJedisPool.getResource();
	}

	/** =============== 当DAO层方法需要传递map参数时 E只是用来定位namespace的 =============== **/
	/**
	 * @设置 redis连接池
	 */
	public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
		this.shardedJedisPool = shardedJedisPool;
	}
}
