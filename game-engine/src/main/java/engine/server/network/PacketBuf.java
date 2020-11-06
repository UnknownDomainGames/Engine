package engine.server.network;

import engine.item.ItemStack;
import engine.math.BlockPos;
import engine.registry.Registries;
import engine.util.Direction;
import engine.world.hit.BlockHitResult;
import engine.world.hit.HitResult;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PacketBuf extends ByteBuf {

    private ByteBuf backingBuffer;

    public PacketBuf(ByteBuf wrapped){
        this.backingBuffer = wrapped;
    }

    public static int getVarIntSize(int input) {
        for(int i = 1; i < 5; ++i) {
            if ((input & -1 << i * 7) == 0) {
                return i;
            }
        }

        return 5;
    }

    public int readVarInt() {
        int i = 0;
        int j = 0;

        while(true) {
            byte b0 = this.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }

            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }

    public PacketBuf writeVarInt(int input) {
        while ((input & -128) != 0) {
            this.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        this.writeByte(input);
        return this;
    }

    public String readString() {
        var len = readVarInt();
        return readCharSequence(len, StandardCharsets.UTF_8).toString();
    }

    public PacketBuf writeString(String string) {
        writeVarInt(string.length());
        writeCharSequence(string, StandardCharsets.UTF_8);
        return this;
    }

    public <T extends Enum<T>> T readEnum(Class<T> type) {
        return type.getEnumConstants()[readVarInt()];
    }

    public void writeEnum(Enum<?> em) {
        writeVarInt(em.ordinal());
    }

    public void writeBlockPos(BlockPos pos) {
        writeVarInt(pos.x());
        writeVarInt(pos.y());
        writeVarInt(pos.z());
    }

    public BlockPos readBlockPos() {
        return BlockPos.of(readVarInt(), readVarInt(), readVarInt());
    }

    public void writeHitResult(HitResult result) {
        if (result.getType() == HitResult.Type.BLOCK) {
            writeByte(1);
            writeBlockHitResult(((BlockHitResult) result));
        }
    }

    private void writeBlockHitResult(BlockHitResult result) {
        if(result instanceof BlockHitResult.Simplified){
            writeString(((BlockHitResult.Simplified) result).getWorldName());
        }
        else {
            writeString(result.getWorld().getName());
        }
        writeBlockPos(result.getPos());
        writeEnum(result.getDirection());
        var hitPoint = result.getHitPoint();
        writeFloat(hitPoint.x());
        writeFloat(hitPoint.y());
        writeFloat(hitPoint.z());
    }

    public HitResult readHitResult() {
        var head = readByte();
        if (head == 1) {
            return readBlockHitResult();
        }
        return null;
    }

    private BlockHitResult.Simplified readBlockHitResult() {
        var worldName = readString();
        var blockPos = readBlockPos();
        var direction = readEnum(Direction.class);
        var hitPt = new Vector3f(readFloat(), readFloat(), readFloat());
        return new BlockHitResult.Simplified(worldName, blockPos, hitPt, direction);
    }

    public void writeItemStack(ItemStack stack) {
        if (stack.isEmpty()) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeVarInt(Registries.getItemRegistry().getId(stack.getItem()));
            writeVarInt(stack.getAmount());
        }
    }

    public ItemStack readItemStack() {
        if (readBoolean()) {
            var item = Registries.getItemRegistry().getValue(readVarInt());
            return new ItemStack(item, readVarInt());
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public int capacity() {
        return backingBuffer.capacity();
    }

    @Override
    public PacketBuf capacity(int newCapacity) {
        return new PacketBuf(backingBuffer.capacity(newCapacity));
    }

    @Override
    public int maxCapacity() {
        return backingBuffer.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return backingBuffer.alloc();
    }

    @Override
    public ByteOrder order() {
        return backingBuffer.order();
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        return backingBuffer.order(endianness);
    }

    @Override
    public ByteBuf unwrap() {
        return backingBuffer;
    }

    @Override
    public boolean isDirect() {
        return backingBuffer.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return backingBuffer.isReadOnly();
    }

    @Override
    public PacketBuf asReadOnly() {
        return new PacketBuf(backingBuffer.asReadOnly());
    }

    @Override
    public int readerIndex() {
        return backingBuffer.readerIndex();
    }

    @Override
    public PacketBuf readerIndex(int readerIndex) {
        backingBuffer.readerIndex(readerIndex);
        return this;
    }

    @Override
    public int writerIndex() {
        return backingBuffer.writerIndex();
    }

    @Override
    public PacketBuf writerIndex(int writerIndex) {
        backingBuffer.writerIndex(writerIndex);
        return this;
    }

    @Override
    public PacketBuf setIndex(int readerIndex, int writerIndex) {
        backingBuffer.setIndex(readerIndex, writerIndex);
        return this;
    }

    @Override
    public int readableBytes() {
        return backingBuffer.readableBytes();
    }

    @Override
    public int writableBytes() {
        return backingBuffer.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return backingBuffer.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return backingBuffer.isReadable();
    }

    @Override
    public boolean isReadable(int size) {
        return backingBuffer.isReadable(size);
    }

    @Override
    public boolean isWritable() {
        return backingBuffer.isWritable();
    }

    @Override
    public boolean isWritable(int size) {
        return backingBuffer.isWritable(size);
    }

    @Override
    public PacketBuf clear() {
        backingBuffer.clear();
        return this;
    }

    @Override
    public PacketBuf markReaderIndex() {
        backingBuffer.markReaderIndex();
        return this;
    }

    @Override
    public PacketBuf resetReaderIndex() {
        backingBuffer.resetReaderIndex();
        return this;
    }

    @Override
    public PacketBuf markWriterIndex() {
        backingBuffer.markWriterIndex();
        return this;
    }

    @Override
    public PacketBuf resetWriterIndex() {
        backingBuffer.resetWriterIndex();
        return this;
    }

    @Override
    public PacketBuf discardReadBytes() {
        backingBuffer.discardReadBytes();
        return this;
    }

    @Override
    public PacketBuf discardSomeReadBytes() {
        backingBuffer.discardSomeReadBytes();
        return this;
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        backingBuffer.ensureWritable(minWritableBytes);
        return this;
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return backingBuffer.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        return backingBuffer.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        return backingBuffer.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return backingBuffer.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return backingBuffer.getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        return backingBuffer.getShortLE(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return backingBuffer.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return backingBuffer.getUnsignedShortLE(index);
    }

    @Override
    public int getMedium(int index) {
        return backingBuffer.getMedium(index);
    }

    @Override
    public int getMediumLE(int index) {
        return backingBuffer.getMediumLE(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return backingBuffer.getUnsignedMedium(index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return backingBuffer.getUnsignedMediumLE(index);
    }

    @Override
    public int getInt(int index) {
        return backingBuffer.getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        return backingBuffer.getIntLE(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return backingBuffer.getUnsignedInt(index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return backingBuffer.getUnsignedIntLE(index);
    }

    @Override
    public long getLong(int index) {
        return backingBuffer.getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        return backingBuffer.getLongLE(index);
    }

    @Override
    public char getChar(int index) {
        return backingBuffer.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return backingBuffer.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return backingBuffer.getDouble(index);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        backingBuffer.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        backingBuffer.getBytes(index, dst, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        backingBuffer.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        backingBuffer.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        backingBuffer.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        backingBuffer.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        backingBuffer.getBytes(index, out, length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return backingBuffer.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return backingBuffer.getBytes(index, out, position, length);
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return backingBuffer.getCharSequence(index, length, charset);
    }

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        backingBuffer.setBoolean(index, value);
        return this;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        backingBuffer.setByte(index, value);
        return this;
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        backingBuffer.setShort(index, value);
        return this;
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        backingBuffer.setShortLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        backingBuffer.setMedium(index, value);
        return this;
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        backingBuffer.setMediumLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        backingBuffer.setInt(index, value);
        return this;
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        backingBuffer.setIntLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        backingBuffer.setLong(index, value);
        return this;
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        backingBuffer.setLongLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        backingBuffer.setChar(index, value);
        return this;
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        backingBuffer.setFloat(index, value);
        return this;
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        backingBuffer.setDouble(index, value);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        backingBuffer.setBytes(index, src);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        backingBuffer.setBytes(index, src, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        backingBuffer.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        backingBuffer.setBytes(index, src);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        backingBuffer.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        backingBuffer.setBytes(index, src);
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return backingBuffer.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return backingBuffer.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return backingBuffer.setBytes(index, in, position, length);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        backingBuffer.setZero(index, length);
        return this;
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return backingBuffer.setCharSequence(index, sequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return backingBuffer.readBoolean();
    }

    @Override
    public byte readByte() {
        return backingBuffer.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return backingBuffer.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return backingBuffer.readShort();
    }

    @Override
    public short readShortLE() {
        return backingBuffer.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return backingBuffer.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return backingBuffer.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return backingBuffer.readMedium();
    }

    @Override
    public int readMediumLE() {
        return backingBuffer.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return backingBuffer.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return backingBuffer.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return backingBuffer.readInt();
    }

    @Override
    public int readIntLE() {
        return backingBuffer.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return backingBuffer.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return backingBuffer.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return backingBuffer.readLong();
    }

    @Override
    public long readLongLE() {
        return backingBuffer.readLongLE();
    }

    @Override
    public char readChar() {
        return backingBuffer.readChar();
    }

    @Override
    public float readFloat() {
        return backingBuffer.readFloat();
    }

    @Override
    public double readDouble() {
        return backingBuffer.readDouble();
    }

    @Override
    public ByteBuf readBytes(int length) {
        return backingBuffer.readBytes(length);
    }

    @Override
    public ByteBuf readSlice(int length) {
        return backingBuffer.readSlice(length);
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return backingBuffer.readRetainedSlice(length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        backingBuffer.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        backingBuffer.readBytes(dst, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        backingBuffer.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        backingBuffer.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        backingBuffer.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        backingBuffer.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        backingBuffer.readBytes(out, length);
        return this;
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return backingBuffer.readBytes(out, length);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return backingBuffer.readCharSequence(length, charset);
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return backingBuffer.readBytes(out, position, length);
    }

    @Override
    public ByteBuf skipBytes(int length) {
        backingBuffer.skipBytes(length);
        return this;
    }

    @Override
    public ByteBuf writeBoolean(boolean value) {
        backingBuffer.writeBoolean(value);
        return this;
    }

    @Override
    public ByteBuf writeByte(int value) {
        backingBuffer.writeByte(value);
        return this;
    }

    @Override
    public ByteBuf writeShort(int value) {
        backingBuffer.writeShort(value);
        return this;
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        backingBuffer.writeShortLE(value);
        return this;
    }

    @Override
    public ByteBuf writeMedium(int value) {
        backingBuffer.writeMedium(value);
        return this;
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        backingBuffer.writeMediumLE(value);
        return this;
    }

    @Override
    public ByteBuf writeInt(int value) {
        backingBuffer.writeInt(value);
        return this;
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        backingBuffer.writeIntLE(value);
        return this;
    }

    @Override
    public ByteBuf writeLong(long value) {
        backingBuffer.writeLong(value);
        return this;
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        backingBuffer.writeLongLE(value);
        return this;
    }

    @Override
    public ByteBuf writeChar(int value) {
        backingBuffer.writeChar(value);
        return this;
    }

    @Override
    public ByteBuf writeFloat(float value) {
        backingBuffer.writeFloat(value);
        return this;
    }

    @Override
    public ByteBuf writeDouble(double value) {
        backingBuffer.writeDouble(value);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        backingBuffer.writeBytes(src);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        backingBuffer.writeBytes(src, length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        backingBuffer.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        backingBuffer.writeBytes(src);
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        backingBuffer.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        backingBuffer.writeBytes(src);
        return this;
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return backingBuffer.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return backingBuffer.writeBytes(in, length);
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return backingBuffer.writeBytes(in, position, length);
    }

    @Override
    public ByteBuf writeZero(int length) {
        backingBuffer.writeZero(length);
        return this;
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return backingBuffer.writeCharSequence(sequence, charset);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return backingBuffer.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        return backingBuffer.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return backingBuffer.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return backingBuffer.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        return backingBuffer.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return backingBuffer.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return backingBuffer.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return backingBuffer.forEachByteDesc(index, length, processor);
    }

    @Override
    public ByteBuf copy() {
        return new PacketBuf(backingBuffer.copy());
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return new PacketBuf(backingBuffer.copy(index, length));
    }

    @Override
    public ByteBuf slice() {
        return new PacketBuf(backingBuffer.slice());
    }

    @Override
    public ByteBuf retainedSlice() {
        return new PacketBuf(backingBuffer.retainedSlice());
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return new PacketBuf(backingBuffer.slice(index, length));
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return new PacketBuf(backingBuffer.retainedSlice(index, length));
    }

    @Override
    public ByteBuf duplicate() {
        return new PacketBuf(backingBuffer.duplicate());
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return new PacketBuf(backingBuffer.retainedDuplicate());
    }

    @Override
    public int nioBufferCount() {
        return backingBuffer.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return backingBuffer.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return backingBuffer.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return backingBuffer.internalNioBuffer(index, length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return backingBuffer.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return backingBuffer.nioBuffers(index, length);
    }

    @Override
    public boolean hasArray() {
        return backingBuffer.hasArray();
    }

    @Override
    public byte[] array() {
        return backingBuffer.array();
    }

    @Override
    public int arrayOffset() {
        return backingBuffer.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return backingBuffer.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return backingBuffer.memoryAddress();
    }

    @Override
    public String toString(Charset charset) {
        return String.format("PacketBuf[backingBuffer=%s]", backingBuffer.toString(charset));
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return String.format("PacketBuf[backingBuffer=%s]", backingBuffer.toString(index, length, charset));
    }

    @Override
    public int hashCode() {
        return backingBuffer.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        else if(obj instanceof PacketBuf){
            return backingBuffer.equals(((PacketBuf) obj).backingBuffer);
        }
        return false;
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        return backingBuffer.compareTo(buffer);
    }

    @Override
    public String toString() {
        return String.format("PacketBuf[backingBuffer=%s]", backingBuffer.toString());
    }

    @Override
    public ByteBuf retain(int increment) {
        return backingBuffer.retain(increment);
    }

    @Override
    public int refCnt() {
        return backingBuffer.refCnt();
    }

    @Override
    public ByteBuf retain() {
        return backingBuffer.retain();
    }

    @Override
    public ByteBuf touch() {
        return backingBuffer.touch();
    }

    @Override
    public ByteBuf touch(Object hint) {
        return backingBuffer.touch(hint);
    }

    @Override
    public boolean release() {
        return backingBuffer.release();
    }

    @Override
    public boolean release(int decrement) {
        return backingBuffer.release(decrement);
    }
}
