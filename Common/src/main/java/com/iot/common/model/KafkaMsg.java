// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: KafkaMsg.proto

package com.iot.common.model;

public final class KafkaMsg {
  private KafkaMsg() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface KafkaMsgPbOrBuilder extends
      // @@protoc_insertion_point(interface_extends:model.KafkaMsgPb)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     *in proto3, [packed=true] is default
     * </pre>
     *
     * <code>repeated string channelId = 1;</code>
     */
    java.util.List<java.lang.String>
        getChannelIdList();
    /**
     * <pre>
     *in proto3, [packed=true] is default
     * </pre>
     *
     * <code>repeated string channelId = 1;</code>
     */
    int getChannelIdCount();
    /**
     * <pre>
     *in proto3, [packed=true] is default
     * </pre>
     *
     * <code>repeated string channelId = 1;</code>
     */
    java.lang.String getChannelId(int index);
    /**
     * <pre>
     *in proto3, [packed=true] is default
     * </pre>
     *
     * <code>repeated string channelId = 1;</code>
     */
    com.google.protobuf.ByteString
        getChannelIdBytes(int index);

    /**
     * <pre>
     *BaseMsg里面的msgId
     * </pre>
     *
     * <code>optional int64 msgId = 2;</code>
     */
    long getMsgId();

    /**
     * <pre>
     *BaseMsg里面的data
     * </pre>
     *
     * <code>optional bytes data = 3;</code>
     */
    com.google.protobuf.ByteString getData();
  }
  /**
   * Protobuf type {@code model.KafkaMsgPb}
   */
  public  static final class KafkaMsgPb extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:model.KafkaMsgPb)
      KafkaMsgPbOrBuilder {
    // Use KafkaMsgPb.newBuilder() to construct.
    private KafkaMsgPb(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private KafkaMsgPb() {
      channelId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      msgId_ = 0L;
      data_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private KafkaMsgPb(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!input.skipField(tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();
              if (!((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
                channelId_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000001;
              }
              channelId_.add(s);
              break;
            }
            case 16: {

              msgId_ = input.readInt64();
              break;
            }
            case 26: {

              data_ = input.readBytes();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        if (((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
          channelId_ = channelId_.getUnmodifiableView();
        }
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.iot.common.model.KafkaMsg.internal_static_model_KafkaMsgPb_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.iot.common.model.KafkaMsg.internal_static_model_KafkaMsgPb_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.iot.common.model.KafkaMsg.KafkaMsgPb.class, com.iot.common.model.KafkaMsg.KafkaMsgPb.Builder.class);
    }

    private int bitField0_;
    public static final int CHANNELID_FIELD_NUMBER = 1;
    private com.google.protobuf.LazyStringList channelId_;
    /**
     * <pre>
     *in proto3, [packed=true] is default
     * </pre>
     *
     * <code>repeated string channelId = 1;</code>
     */
    public com.google.protobuf.ProtocolStringList
        getChannelIdList() {
      return channelId_;
    }
    /**
     * <pre>
     *in proto3, [packed=true] is default
     * </pre>
     *
     * <code>repeated string channelId = 1;</code>
     */
    public int getChannelIdCount() {
      return channelId_.size();
    }
    /**
     * <pre>
     *in proto3, [packed=true] is default
     * </pre>
     *
     * <code>repeated string channelId = 1;</code>
     */
    public java.lang.String getChannelId(int index) {
      return channelId_.get(index);
    }
    /**
     * <pre>
     *in proto3, [packed=true] is default
     * </pre>
     *
     * <code>repeated string channelId = 1;</code>
     */
    public com.google.protobuf.ByteString
        getChannelIdBytes(int index) {
      return channelId_.getByteString(index);
    }

    public static final int MSGID_FIELD_NUMBER = 2;
    private long msgId_;
    /**
     * <pre>
     *BaseMsg里面的msgId
     * </pre>
     *
     * <code>optional int64 msgId = 2;</code>
     */
    public long getMsgId() {
      return msgId_;
    }

    public static final int DATA_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString data_;
    /**
     * <pre>
     *BaseMsg里面的data
     * </pre>
     *
     * <code>optional bytes data = 3;</code>
     */
    public com.google.protobuf.ByteString getData() {
      return data_;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      for (int i = 0; i < channelId_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, channelId_.getRaw(i));
      }
      if (msgId_ != 0L) {
        output.writeInt64(2, msgId_);
      }
      if (!data_.isEmpty()) {
        output.writeBytes(3, data_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      {
        int dataSize = 0;
        for (int i = 0; i < channelId_.size(); i++) {
          dataSize += computeStringSizeNoTag(channelId_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getChannelIdList().size();
      }
      if (msgId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(2, msgId_);
      }
      if (!data_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, data_);
      }
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.iot.common.model.KafkaMsg.KafkaMsgPb)) {
        return super.equals(obj);
      }
      com.iot.common.model.KafkaMsg.KafkaMsgPb other = (com.iot.common.model.KafkaMsg.KafkaMsgPb) obj;

      boolean result = true;
      result = result && getChannelIdList()
          .equals(other.getChannelIdList());
      result = result && (getMsgId()
          == other.getMsgId());
      result = result && getData()
          .equals(other.getData());
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptorForType().hashCode();
      if (getChannelIdCount() > 0) {
        hash = (37 * hash) + CHANNELID_FIELD_NUMBER;
        hash = (53 * hash) + getChannelIdList().hashCode();
      }
      hash = (37 * hash) + MSGID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getMsgId());
      hash = (37 * hash) + DATA_FIELD_NUMBER;
      hash = (53 * hash) + getData().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.iot.common.model.KafkaMsg.KafkaMsgPb parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.iot.common.model.KafkaMsg.KafkaMsgPb parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.iot.common.model.KafkaMsg.KafkaMsgPb parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.iot.common.model.KafkaMsg.KafkaMsgPb parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.iot.common.model.KafkaMsg.KafkaMsgPb parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.iot.common.model.KafkaMsg.KafkaMsgPb parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.iot.common.model.KafkaMsg.KafkaMsgPb parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.iot.common.model.KafkaMsg.KafkaMsgPb parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.iot.common.model.KafkaMsg.KafkaMsgPb parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.iot.common.model.KafkaMsg.KafkaMsgPb parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.iot.common.model.KafkaMsg.KafkaMsgPb prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code model.KafkaMsgPb}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:model.KafkaMsgPb)
        com.iot.common.model.KafkaMsg.KafkaMsgPbOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.iot.common.model.KafkaMsg.internal_static_model_KafkaMsgPb_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.iot.common.model.KafkaMsg.internal_static_model_KafkaMsgPb_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.iot.common.model.KafkaMsg.KafkaMsgPb.class, com.iot.common.model.KafkaMsg.KafkaMsgPb.Builder.class);
      }

      // Construct using com.iot.common.model.KafkaMsg.KafkaMsgPb.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        channelId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        msgId_ = 0L;

        data_ = com.google.protobuf.ByteString.EMPTY;

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.iot.common.model.KafkaMsg.internal_static_model_KafkaMsgPb_descriptor;
      }

      public com.iot.common.model.KafkaMsg.KafkaMsgPb getDefaultInstanceForType() {
        return com.iot.common.model.KafkaMsg.KafkaMsgPb.getDefaultInstance();
      }

      public com.iot.common.model.KafkaMsg.KafkaMsgPb build() {
        com.iot.common.model.KafkaMsg.KafkaMsgPb result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.iot.common.model.KafkaMsg.KafkaMsgPb buildPartial() {
        com.iot.common.model.KafkaMsg.KafkaMsgPb result = new com.iot.common.model.KafkaMsg.KafkaMsgPb(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((bitField0_ & 0x00000001) == 0x00000001)) {
          channelId_ = channelId_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.channelId_ = channelId_;
        result.msgId_ = msgId_;
        result.data_ = data_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.iot.common.model.KafkaMsg.KafkaMsgPb) {
          return mergeFrom((com.iot.common.model.KafkaMsg.KafkaMsgPb)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.iot.common.model.KafkaMsg.KafkaMsgPb other) {
        if (other == com.iot.common.model.KafkaMsg.KafkaMsgPb.getDefaultInstance()) return this;
        if (!other.channelId_.isEmpty()) {
          if (channelId_.isEmpty()) {
            channelId_ = other.channelId_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureChannelIdIsMutable();
            channelId_.addAll(other.channelId_);
          }
          onChanged();
        }
        if (other.getMsgId() != 0L) {
          setMsgId(other.getMsgId());
        }
        if (other.getData() != com.google.protobuf.ByteString.EMPTY) {
          setData(other.getData());
        }
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.iot.common.model.KafkaMsg.KafkaMsgPb parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.iot.common.model.KafkaMsg.KafkaMsgPb) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private com.google.protobuf.LazyStringList channelId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureChannelIdIsMutable() {
        if (!((bitField0_ & 0x00000001) == 0x00000001)) {
          channelId_ = new com.google.protobuf.LazyStringArrayList(channelId_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <pre>
       *in proto3, [packed=true] is default
       * </pre>
       *
       * <code>repeated string channelId = 1;</code>
       */
      public com.google.protobuf.ProtocolStringList
          getChannelIdList() {
        return channelId_.getUnmodifiableView();
      }
      /**
       * <pre>
       *in proto3, [packed=true] is default
       * </pre>
       *
       * <code>repeated string channelId = 1;</code>
       */
      public int getChannelIdCount() {
        return channelId_.size();
      }
      /**
       * <pre>
       *in proto3, [packed=true] is default
       * </pre>
       *
       * <code>repeated string channelId = 1;</code>
       */
      public java.lang.String getChannelId(int index) {
        return channelId_.get(index);
      }
      /**
       * <pre>
       *in proto3, [packed=true] is default
       * </pre>
       *
       * <code>repeated string channelId = 1;</code>
       */
      public com.google.protobuf.ByteString
          getChannelIdBytes(int index) {
        return channelId_.getByteString(index);
      }
      /**
       * <pre>
       *in proto3, [packed=true] is default
       * </pre>
       *
       * <code>repeated string channelId = 1;</code>
       */
      public Builder setChannelId(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureChannelIdIsMutable();
        channelId_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <pre>
       *in proto3, [packed=true] is default
       * </pre>
       *
       * <code>repeated string channelId = 1;</code>
       */
      public Builder addChannelId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureChannelIdIsMutable();
        channelId_.add(value);
        onChanged();
        return this;
      }
      /**
       * <pre>
       *in proto3, [packed=true] is default
       * </pre>
       *
       * <code>repeated string channelId = 1;</code>
       */
      public Builder addAllChannelId(
          java.lang.Iterable<java.lang.String> values) {
        ensureChannelIdIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, channelId_);
        onChanged();
        return this;
      }
      /**
       * <pre>
       *in proto3, [packed=true] is default
       * </pre>
       *
       * <code>repeated string channelId = 1;</code>
       */
      public Builder clearChannelId() {
        channelId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      /**
       * <pre>
       *in proto3, [packed=true] is default
       * </pre>
       *
       * <code>repeated string channelId = 1;</code>
       */
      public Builder addChannelIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureChannelIdIsMutable();
        channelId_.add(value);
        onChanged();
        return this;
      }

      private long msgId_ ;
      /**
       * <pre>
       *BaseMsg里面的msgId
       * </pre>
       *
       * <code>optional int64 msgId = 2;</code>
       */
      public long getMsgId() {
        return msgId_;
      }
      /**
       * <pre>
       *BaseMsg里面的msgId
       * </pre>
       *
       * <code>optional int64 msgId = 2;</code>
       */
      public Builder setMsgId(long value) {
        
        msgId_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *BaseMsg里面的msgId
       * </pre>
       *
       * <code>optional int64 msgId = 2;</code>
       */
      public Builder clearMsgId() {
        
        msgId_ = 0L;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <pre>
       *BaseMsg里面的data
       * </pre>
       *
       * <code>optional bytes data = 3;</code>
       */
      public com.google.protobuf.ByteString getData() {
        return data_;
      }
      /**
       * <pre>
       *BaseMsg里面的data
       * </pre>
       *
       * <code>optional bytes data = 3;</code>
       */
      public Builder setData(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        data_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *BaseMsg里面的data
       * </pre>
       *
       * <code>optional bytes data = 3;</code>
       */
      public Builder clearData() {
        
        data_ = getDefaultInstance().getData();
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:model.KafkaMsgPb)
    }

    // @@protoc_insertion_point(class_scope:model.KafkaMsgPb)
    private static final com.iot.common.model.KafkaMsg.KafkaMsgPb DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.iot.common.model.KafkaMsg.KafkaMsgPb();
    }

    public static com.iot.common.model.KafkaMsg.KafkaMsgPb getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<KafkaMsgPb>
        PARSER = new com.google.protobuf.AbstractParser<KafkaMsgPb>() {
      public KafkaMsgPb parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new KafkaMsgPb(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<KafkaMsgPb> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<KafkaMsgPb> getParserForType() {
      return PARSER;
    }

    public com.iot.common.model.KafkaMsg.KafkaMsgPb getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_model_KafkaMsgPb_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_model_KafkaMsgPb_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016KafkaMsg.proto\022\005model\"<\n\nKafkaMsgPb\022\021\n" +
      "\tchannelId\030\001 \003(\t\022\r\n\005msgId\030\002 \001(\003\022\014\n\004data\030" +
      "\003 \001(\014B \n\024com.iot.common.modelB\010KafkaMsgb" +
      "\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_model_KafkaMsgPb_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_model_KafkaMsgPb_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_model_KafkaMsgPb_descriptor,
        new java.lang.String[] { "ChannelId", "MsgId", "Data", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}