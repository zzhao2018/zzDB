package IndexEngine.LSM;

import lombok.Data;

@Data
public class BlockOffsetPair {
    private Long offset;
    private Long block;

    public BlockOffsetPair(Long offset, Long block) {
        this.block = block;
        this.offset = offset;
    }
}
