package com.bespectacled.modernbeta.api.world.gen;

import java.util.stream.IntStream;

import com.bespectacled.modernbeta.util.BlockStates;
import com.bespectacled.modernbeta.util.chunk.ChunkCache;
import com.bespectacled.modernbeta.util.chunk.HeightmapChunk;
import com.bespectacled.modernbeta.util.pool.DoubleArrayPool;
import com.bespectacled.modernbeta.world.gen.OldChunkGenerator;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;

public abstract class NoiseChunkProvider extends BaseChunkProvider {
    protected final int verticalNoiseResolution;   // Number of blocks in a vertical subchunk
    protected final int horizontalNoiseResolution; // Number of blocks in a horizontal subchunk 
    
    protected final int noiseSizeX; // Number of horizontal subchunks along x
    protected final int noiseSizeZ; // Number of horizontal subchunks along z
    protected final int noiseSizeY; // Number of vertical subchunks
    protected final int noiseMinY;  // Subchunk index of bottom of the world
    protected final int noiseTopY;  // Number of positive (y >= 0) vertical subchunks

    protected final double xzScale;
    protected final double yScale;
    
    protected final double xzFactor;
    protected final double yFactor;
    
    protected final int topSlideTarget;
    protected final int topSlideSize;
    protected final int topSlideOffset;
    
    protected final int bottomSlideTarget;
    protected final int bottomSlideSize;
    protected final int bottomSlideOffset;
    
    protected final ChunkCache<double[]> noiseChunkCache;
    protected final ChunkCache<HeightmapChunk> heightmapChunkCache;
    
    protected final DoubleArrayPool surfaceNoisePool;

    public NoiseChunkProvider(OldChunkGenerator chunkGenerator) {
        this(
            chunkGenerator,
            0,
            chunkGenerator.getGeneratorSettings().get().getGenerationShapeConfig().getHeight(),
            chunkGenerator.getGeneratorSettings().get().getSeaLevel(),
            0,
            chunkGenerator.getGeneratorSettings().get().getBedrockFloorY(),
            chunkGenerator.getGeneratorSettings().get().getBedrockCeilingY(),
            chunkGenerator.getGeneratorSettings().get().getDefaultBlock(),
            chunkGenerator.getGeneratorSettings().get().getDefaultFluid(),
            chunkGenerator.getGeneratorSettings().get().getGenerationShapeConfig().getSizeVertical(),
            chunkGenerator.getGeneratorSettings().get().getGenerationShapeConfig().getSizeHorizontal(),
            chunkGenerator.getGeneratorSettings().get().getGenerationShapeConfig().getSampling().getXZScale(),
            chunkGenerator.getGeneratorSettings().get().getGenerationShapeConfig().getSampling().getYScale(),
            chunkGenerator.getGeneratorSettings().get().getGenerationShapeConfig().getSampling().getXZFactor(),
            chunkGenerator.getGeneratorSettings().get().getGenerationShapeConfig().getSampling().getYFactor(),
            chunkGenerator.getGeneratorSettings().get().getGenerationShapeConfig().getTopSlide().getTarget(),
            chunkGenerator.getGeneratorSettings().get().getGenerationShapeConfig().getTopSlide().getSize(),
            chunkGenerator.getGeneratorSettings().get().getGenerationShapeConfig().getTopSlide().getOffset(),
            chunkGenerator.getGeneratorSettings().get().getGenerationShapeConfig().getBottomSlide().getTarget(),
            chunkGenerator.getGeneratorSettings().get().getGenerationShapeConfig().getBottomSlide().getSize(),
            chunkGenerator.getGeneratorSettings().get().getGenerationShapeConfig().getBottomSlide().getOffset()
        );
    }

    public NoiseChunkProvider(
        OldChunkGenerator chunkGenerator,
        int minY, 
        int worldHeight, 
        int seaLevel,
        int minSurfaceY,
        int bedrockFloor,
        int bedrockCeiling,
        BlockState defaultBlock,
        BlockState defaultFluid,
        int sizeVertical, 
        int sizeHorizontal,
        double xzScale, 
        double yScale,
        double xzFactor, 
        double yFactor,
        int topSlideTarget,
        int topSlideSize,
        int topSlideOffset,
        int bottomSlideTarget,
        int bottomSlideSize,
        int bottomSlideOffset
    ) {
        super(chunkGenerator, minY, worldHeight, seaLevel, minSurfaceY, bedrockFloor, bedrockCeiling, defaultBlock, defaultFluid);
        
        this.verticalNoiseResolution = sizeVertical * 4;
        this.horizontalNoiseResolution = sizeHorizontal * 4;
        
        this.noiseSizeX = 16 / this.horizontalNoiseResolution;
        this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
        this.noiseSizeY = MathHelper.floorDiv(this.worldHeight, this.verticalNoiseResolution);
        this.noiseMinY = MathHelper.floorDiv(this.minY, this.verticalNoiseResolution);
        this.noiseTopY = MathHelper.floorDiv(this.minY + this.worldHeight, this.verticalNoiseResolution);
        
        this.xzScale = xzScale;
        this.yScale = yScale;
        
        this.xzFactor = xzFactor;
        this.yFactor = yFactor;
        
        this.topSlideTarget = topSlideTarget;
        this.topSlideSize = topSlideSize;
        this.topSlideOffset = topSlideOffset;
        
        this.bottomSlideTarget = bottomSlideTarget;
        this.bottomSlideSize = bottomSlideSize;
        this.bottomSlideOffset = bottomSlideOffset;
        
        // Chunk caches
        this.noiseChunkCache = new ChunkCache<>(
            "noise", 
            1536, 
            true, 
            (chunkX, chunkZ) -> this.generateNoiseArr(chunkX * this.noiseSizeX, chunkZ * this.noiseSizeZ)
        );
        
        this.heightmapChunkCache = new ChunkCache<>(
            "heightmap", 
            1536, 
            true, 
            this::sampleHeightmap
        );
        
        // Noise array pools
        this.surfaceNoisePool = new DoubleArrayPool(64, arr -> arr.length == 256, 256);
    }
    
    /**
     * 
     * @return Number of blocks in a vertical subchunk.
     */
    public int getVerticalNoiseResolution() {
        return this.verticalNoiseResolution;
    }
    
    /**
     * Generates base terrain for given chunk and returns it.
     * 
     * @param worldAccess
     * @param structureAccessor
     * @param chunk
     * 
     * @return A completed chunk.
     */
    @Override
    public void provideChunk(WorldAccess worldAccess, StructureAccessor structureAccessor, Chunk chunk) {
        this.generateTerrain(chunk, structureAccessor);
    }
    
    /**
     * Sample height at given x/z coordinate. Initially generates heightmap for entire chunk, 
     * if chunk containing x/z coordinates has never been sampled.
     *
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param type Vanilla heightmap type.
     * @param world
     * 
     * @return The y-coordinate of top block at x/z.
     */
    @Override
    public int getHeight(int x, int z, Heightmap.Type type) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        int groundHeight = this.heightmapChunkCache.get(chunkX, chunkZ).getHeight(x, z);
        
        // Not ideal
        if (type == Heightmap.Type.WORLD_SURFACE_WG && groundHeight < this.seaLevel)
            groundHeight = this.seaLevel;

        return groundHeight;
    }
    
    /**
     * Generates noise modifiers to be stored in <code>scaleDepth</code> array.
     * 
     * @param startNoiseX x-coordinate start of chunk in noise coordinates.
     * @param startNoiseZ z-coordinate start of chunk in noise coordinates.
     * @param curNoiseX Current subchunk index along x-axis.
     * @param curNoiseZ Current subchunk index along z-axis.
     * @param scaleDepth Array to hold noise modifiers.
     */
    protected abstract void generateScaleDepth(int startNoiseX, int startNoiseZ, int curNoiseX, int curNoiseZ, double[] scaleDepth);
    
    /**
     * Generates a noise density for a given noise coordinates and noise modifiers from <code>scaleDepth</code> array.
     * 
     * @param noiseX x-coordinate in noise coordinates.
     * @param noiseY y-coordinate in noise coordinates.
     * @param noiseZ z-coordinate in noise coordinates.
     * @param scaleDepth Array holding noise modifiers.
     * @return
     */
    protected abstract double generateNoise(int noiseX, int noiseY, int noiseZ, double[] scaleDepth);
    
    /**
     * Generates noise values for entire chunk starting at <code>noiseX</code> and <code>noiseZ</code>, to be stored in <code>noise</code> array.
     * 
     * @param noiseX x-coordinate start of the chunk in noise coordinates.
     * @param noiseZ z-coordinate start of the chunk in noise coordinates.
     * @param noise Array to hold all noise values generated by {@link #generateNoise(int, int, int, double[])}.
     */
    protected void generateNoiseArr(int noiseX, int noiseZ, double[] noise) {
        int noiseResolutionX = this.noiseSizeX + 1;
        int noiseResolutionZ = this.noiseSizeZ + 1;
        int noiseResolutionY = this.noiseSizeY + 1;
        
        double[] scaleDepth = new double[4];
        
        int ndx = 0;
        for (int nX = 0; nX < noiseResolutionX; ++nX) {
            for (int nZ = 0; nZ < noiseResolutionZ; ++nZ) {
                this.generateScaleDepth(noiseX, noiseZ, nX, nZ, scaleDepth);
                
                for (int nY = this.noiseMinY; nY < noiseResolutionY + this.noiseMinY; ++nY) {
                    noise[ndx] = this.generateNoise(noiseX + nX, nY, noiseZ + nZ, scaleDepth);
                    
                    ndx++;
                }
            }
        }
    }
    
    /**
     * Generates noise values for entire chunk starting at <code>noiseX</code> and <code>noiseZ</code>.
     * 
     * @param noiseX x-coordinate start of the chunk in noise coordinates.
     * @param noiseZ z-coordinate start of the chunk in noise coordinates.
     * 
     * @returns Populated noise array.
     */
    protected double[] generateNoiseArr(int noiseX, int noiseZ) {
        int noiseResolutionX = this.noiseSizeX + 1;
        int noiseResolutionZ = this.noiseSizeZ + 1;
        int noiseResolutionY = this.noiseSizeY + 1;
        
        double[] noise = new double[(this.noiseSizeX + 1) * (this.noiseSizeZ + 1) * (this.noiseSizeY + 1)];
        double[] scaleDepth = new double[4];
        
        int ndx = 0;
        for (int nX = 0; nX < noiseResolutionX; ++nX) {
            for (int nZ = 0; nZ < noiseResolutionZ; ++nZ) {
                this.generateScaleDepth(noiseX, noiseZ, nX, nZ, scaleDepth);
                
                for (int nY = this.noiseMinY; nY < noiseResolutionY + this.noiseMinY; ++nY) {
                    noise[ndx] = this.generateNoise(noiseX + nX, nY, noiseZ + nZ, scaleDepth);
                    
                    ndx++;
                }
            }
        }
        
        return noise;
    }
    
    /**
     * Generates the base terrain for a given chunk.
     * 
     * @param chunk
     * @param structureAccessor Collects structures within the chunk, so that terrain can be modified to accommodate them.
     */
    protected void generateTerrain(Chunk chunk, StructureAccessor structureAccessor) {
        int noiseResolutionY = this.noiseSizeY + 1;
        int noiseResolutionXZ = this.noiseSizeX + 1;
        
        ChunkPos chunkPos = chunk.getPos();
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        Heightmap heightmapOCEAN = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmapSURFACE = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        
        StructureWeightSampler structureWeightSampler = new StructureWeightSampler(structureAccessor, chunk);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        
        // Get and populate primary noise array
        double[] noise = this.noiseChunkCache.get(chunkX, chunkZ);
        
        for (int subChunkX = 0; subChunkX < this.noiseSizeX; ++subChunkX) {
            for (int subChunkZ = 0; subChunkZ < this.noiseSizeZ; ++ subChunkZ) {
                for (int subChunkY = 0; subChunkY < this.noiseSizeY; ++subChunkY) {
                    double lowerNW = noise[((subChunkX + 0) * noiseResolutionXZ + (subChunkZ + 0)) * noiseResolutionY + (subChunkY + 0)];
                    double lowerSW = noise[((subChunkX + 0) * noiseResolutionXZ + (subChunkZ + 1)) * noiseResolutionY + (subChunkY + 0)];
                    double lowerNE = noise[((subChunkX + 1) * noiseResolutionXZ + (subChunkZ + 0)) * noiseResolutionY + (subChunkY + 0)];
                    double lowerSE = noise[((subChunkX + 1) * noiseResolutionXZ + (subChunkZ + 1)) * noiseResolutionY + (subChunkY + 0)];
                    
                    double upperNW = noise[((subChunkX + 0) * noiseResolutionXZ + (subChunkZ + 0)) * noiseResolutionY + (subChunkY + 1)]; 
                    double upperSW = noise[((subChunkX + 0) * noiseResolutionXZ + (subChunkZ + 1)) * noiseResolutionY + (subChunkY + 1)];
                    double upperNE = noise[((subChunkX + 1) * noiseResolutionXZ + (subChunkZ + 0)) * noiseResolutionY + (subChunkY + 1)];
                    double upperSE = noise[((subChunkX + 1) * noiseResolutionXZ + (subChunkZ + 1)) * noiseResolutionY + (subChunkY + 1)];
                    
                    for (int subY = 0; subY < this.verticalNoiseResolution; ++subY) {
                        int y = subChunkY * this.verticalNoiseResolution + subY;
                        y += this.minY;
                        
                        double deltaY = subY / (double)this.verticalNoiseResolution;
                        
                        double nw = MathHelper.lerp(deltaY, lowerNW, upperNW);
                        double sw = MathHelper.lerp(deltaY, lowerSW, upperSW);
                        double ne = MathHelper.lerp(deltaY, lowerNE, upperNE);
                        double se = MathHelper.lerp(deltaY, lowerSE, upperSE);
                        
                        for (int subX = 0; subX < this.horizontalNoiseResolution; ++subX) {
                            int relX = subX + subChunkX * this.horizontalNoiseResolution;
                            int x = startX + relX;
                            
                            double deltaX = subX / (double)this.horizontalNoiseResolution;
                            
                            double n = MathHelper.lerp(deltaX, nw, ne);
                            double s = MathHelper.lerp(deltaX, sw, se);
                            
                            for (int subZ = 0; subZ < this.horizontalNoiseResolution; ++subZ) {
                                int relZ = subZ + subChunkZ * this.horizontalNoiseResolution;
                                int z = startZ + relZ;
                                
                                double deltaZ = subZ / (double)this.horizontalNoiseResolution;
                                
                                double density = MathHelper.lerp(deltaZ, n, s);
                                
                                BlockState blockToSet = this.getBlockState(
                                    structureWeightSampler,
                                    x, y, z, 
                                    density
                                );
                                
                                chunk.setBlockState(mutable.set(relX, y, relZ), blockToSet, false);
                                
                                heightmapOCEAN.trackUpdate(relX, y, relZ, blockToSet);
                                heightmapSURFACE.trackUpdate(relX, y, relZ, blockToSet);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Generates a heightmap for the chunk containing the given x/z coordinates
     * and returns to {@link #getHeight(int, int, net.minecraft.world.Heightmap.Type, HeightLimitView)} 
     * to cache and return the height.
     * 
     * @param chunkX x-coordinate in chunk coordinates to sample all y-values for.
     * @param chunkZ z-coordinate in chunk coordinates to sample all y-values for.
     * 
     * @return A HeightmapChunk, containing an array of ints containing the heights for the entire chunk.
     */
    protected HeightmapChunk sampleHeightmap(int chunkX, int chunkZ) {
        int noiseResolutionY = this.noiseSizeY + 1;
        int noiseResolutionXZ = this.noiseSizeX + 1;
        
        double[] noise = this.noiseChunkCache.get(chunkX, chunkZ);
        
        int[] heightmap = new int[256];
        IntStream.range(0, heightmap.length).forEach(i -> heightmap[i] = 16);

        for (int subChunkX = 0; subChunkX < this.noiseSizeX; ++subChunkX) {
            for (int subChunkZ = 0; subChunkZ < this.noiseSizeZ; ++subChunkZ) {
                for (int subChunkY = 0; subChunkY < this.noiseSizeY; ++subChunkY) {
                    double lowerNW = noise[((subChunkX + 0) * noiseResolutionXZ + (subChunkZ + 0)) * noiseResolutionY + (subChunkY + 0)];
                    double lowerSW = noise[((subChunkX + 0) * noiseResolutionXZ + (subChunkZ + 1)) * noiseResolutionY + (subChunkY + 0)];
                    double lowerNE = noise[((subChunkX + 1) * noiseResolutionXZ + (subChunkZ + 0)) * noiseResolutionY + (subChunkY + 0)];
                    double lowerSE = noise[((subChunkX + 1) * noiseResolutionXZ + (subChunkZ + 1)) * noiseResolutionY + (subChunkY + 0)];
                    
                    double upperNW = noise[((subChunkX + 0) * noiseResolutionXZ + (subChunkZ + 0)) * noiseResolutionY + (subChunkY + 1)]; 
                    double upperSW = noise[((subChunkX + 0) * noiseResolutionXZ + (subChunkZ + 1)) * noiseResolutionY + (subChunkY + 1)];
                    double upperNE = noise[((subChunkX + 1) * noiseResolutionXZ + (subChunkZ + 0)) * noiseResolutionY + (subChunkY + 1)];
                    double upperSE = noise[((subChunkX + 1) * noiseResolutionXZ + (subChunkZ + 1)) * noiseResolutionY + (subChunkY + 1)];
                    
                    for (int subY = 0; subY < this.verticalNoiseResolution; ++subY) {
                        int y = subChunkY * this.verticalNoiseResolution + subY;
                        y += this.minY;
                        
                        double deltaY = subY / (double)this.verticalNoiseResolution;
                        
                        double nw = MathHelper.lerp(deltaY, lowerNW, upperNW);
                        double sw = MathHelper.lerp(deltaY, lowerSW, upperSW);
                        double ne = MathHelper.lerp(deltaY, lowerNE, upperNE);
                        double se = MathHelper.lerp(deltaY, lowerSE, upperSE);
                        
                        for (int subX = 0; subX < this.horizontalNoiseResolution; ++subX) {
                            int x = subX + subChunkX * this.horizontalNoiseResolution;
                            
                            double deltaX = subX / (double)this.horizontalNoiseResolution;
                            
                            double n = MathHelper.lerp(deltaX, nw, ne);
                            double s = MathHelper.lerp(deltaX, sw, se);
                            
                            for (int subZ = 0; subZ < this.horizontalNoiseResolution; ++subZ) {
                                int z = subZ + subChunkZ * this.horizontalNoiseResolution;
                                
                                double deltaZ = subZ / (double)this.horizontalNoiseResolution;
                                
                                double density = MathHelper.lerp(deltaZ, n, s);
                                
                                if (density > 0.0) {
                                    heightmap[z + x * 16] = y + 1;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Construct new heightmap cache from generated heightmap array
        return new HeightmapChunk(heightmap);
    }
    
    /**
     * Gets blockstate at block coordinates given noise density.
     * 
     * @param weightSampler Sampler used to add/subtract density if a structure start is at coordinate.
     * @param aquiferSampler Sampler used to adjust local water levels for noise caves.
     * @param x x-coordinate in absolute block coordinates.
     * @param y y-coordinate in absolute block coordinates.
     * @param z z-coordinate in absolute block coordinates.
     * @param density Calculated noise density.
     * 
     * @return A blockstate, usually air, stone, or water.
     */
    protected BlockState getBlockState(StructureWeightSampler weightSampler, int x, int y, int z, double density) {
        double clampedDensity = MathHelper.clamp(density / 200.0, -1.0, 1.0);
        clampedDensity = clampedDensity / 2.0 - clampedDensity * clampedDensity * clampedDensity / 24.0;
        clampedDensity += weightSampler.getWeight(x, y, z);
        
        BlockState blockStateToSet = BlockStates.AIR;
        if (clampedDensity > 0.0) {
            blockStateToSet = this.defaultBlock;
        } else if (y < this.getSeaLevel()) {
            blockStateToSet = this.defaultFluid;
        }
        
        return blockStateToSet;
    }
    
    /**
     * Interpolates density to set terrain curve at bottom of the world.
     * 
     * @param density Base density.
     * @param noiseY y-coordinate in noise coordinates.
     * @param initialOffset Initial noise y-coordinate offset. Generator settings offset is subtracted from this.
     * 
     * @return Modified noise density.
     */
    protected double applyBottomSlide(double density, int noiseY, int initialOffset) {
        int bottomSlideStart = this.noiseMinY - initialOffset - this.bottomSlideOffset;
        if (noiseY < bottomSlideStart) {
            double bottomSlideDelta = (float) (bottomSlideStart - noiseY) / ((float) this.bottomSlideSize);
            density = MathHelper.lerp(bottomSlideDelta, density, this.bottomSlideTarget);
        }
        
        return density;
    }
    
    /**
     * Interpolates density to set terrain curve at top of the world.
     * 
     * @param density Base density.
     * @param noiseY y-coordinate in noise coordinates.
     * @param initialOffset Initial noise y-coordinate offset. Generator settings offset is subtracted from this.
     * 
     * @return Modified noise density.
     */
    protected double applyTopSlide(double density, int noiseY, int initialOffset) {
        int topSlideStart = (this.noiseSizeY + this.noiseMinY + 1) - initialOffset - this.topSlideOffset;
        if (noiseY > topSlideStart) {
            double topSlideDelta = (float) (noiseY - topSlideStart) / (float) this.topSlideSize;
            density = MathHelper.lerp(topSlideDelta, density, this.topSlideTarget);
        }
        
        return density;
    }
    
    /**
     * Interpolates density to set terrain curve at top of the world.
     * Height target is provided to set curve at a lower world height than the actual.
     * TODO: Check back later to see if this will work with new mountains.
     * 
     * @param density Base density.
     * @param noiseY y-coordinate in noise coordinates.
     * @param initialOffset Initial noise y-coordinate offset. Generator settings offset is subtracted from this.
     * @param heightTarget The world height the curve should target.
     * 
     * @return Modified noise density.
     */
    protected double applyTopSlide(double density, int noiseY, int initialOffset, int heightTarget) {
        int noiseSizeY = MathHelper.floorDiv(heightTarget, this.verticalNoiseResolution);
        int topSlideStart = (noiseSizeY + 1) - initialOffset - this.topSlideOffset;
        if (noiseY > topSlideStart) {
            // Clamp delta since difference of noiseY and slideStart can exceed slideSize if real world height is larger than provided target "height"
            double topSlideDelta = MathHelper.clamp((float) (noiseY - topSlideStart) / (float) this.topSlideSize, 0.0D, 1.0D);
            density = MathHelper.lerp(topSlideDelta, density, this.topSlideTarget);
        }
        
        return density;
    }
}

