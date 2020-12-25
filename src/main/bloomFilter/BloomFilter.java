package bloomFilter;

import java.util.BitSet;

/**
 * 布隆过滤器由位数组与哈希函数构成
 * 位数组就是数组的每个元素都只占用 1 bit 。每个元素只能是 0 或者 1。这样申请一个 10000 个元素的位数组只占用 10000 / 8 = 1250 B 的空间。
 * K 个哈希函数。
 *
 * 具体实现
 * 使用 K 个哈希函数对元素值进行 K 次计算，得到 K 个哈希值。
 * 根据得到的哈希值，在位数组中把对应下标的值置为 1。
 * 当要判断一个值是否在布隆过滤器中，对元素再次进行哈希计算，得到值之后判断位数组中的每个元素是否都为 1，如果值都为 1，那么说明这个值在布隆过滤器中，如果存在一个值不为 1，说明该元素不在布隆过滤器中。
 */
public class BloomFilter {
    private static final int DEFAULT_SIZE = 2 << 24;//布隆过滤器的比特长度

    /**
     * string.hashcode s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1] 31
     * 这里要选取质数，能很好的降低错误率
     * {3, 5, 7, 11, 13, 31, 37, 61}
     */
    private static final int[] seeds = {3, 5, 7, 11, 13, 31, 37, 61};

    private static BitSet bits = new BitSet(DEFAULT_SIZE);

    private static SimpleHash[] func = new SimpleHash[seeds.length];


    public static void addValue(String value) {
        for (SimpleHash f : func)//将字符串value哈希为8个或多个整数，然后在这些整数的bit上变为1
            bits.set(f.hash(value), true);
    }


    public static void add(String value) {
        if (value != null) addValue(value);
    }


    public static boolean contains(String value) {
        if (value == null) return false;
        boolean ret = true;
        /**
         * todo
         * 这里其实没必要全部跑完，只要一次ret==false那么就不包含这个字符串
         */
        for (SimpleHash f : func)
            ret = ret && bits.get(f.hash(value));
        return ret;
    }


    /**
     * seeds = {31}
     * 15445242
     * seeds = {3, 5, 7, 11, 13, 31, 37, 61}
     * {7143178, 8662090, 10952122, 12929706, 15445242, 18861866, 28330042, 31074170}
     */
    public static void main(String[] args) {
        String value = "132123123123";
        for (int i = 0; i < seeds.length; i++) {
            func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
        }
        add(value);
        System.out.println(contains("132123123123"));
        System.out.println(bits);
    }
}


class SimpleHash {//这玩意相当于C++中的结构体


    private int cap;

    private int seed;


    public SimpleHash(int cap, int seed) {
        this.cap = cap;
        this.seed = seed;
    }


    public int hash(String value) {//字符串哈希，选取好的哈希函数很重要
        int result = 0;
        int len = value.length();
        for (int i = 0; i < len; i++) {
            result = seed * result + value.charAt(i);
        }
        return (cap - 1) & result;
    }
}
