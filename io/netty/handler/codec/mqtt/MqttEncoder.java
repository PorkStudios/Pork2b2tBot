/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.mqtt.MqttCodecUtil;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttIdentifierRejectedException;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubAckPayload;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttSubscribePayload;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribePayload;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.EmptyArrays;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

@ChannelHandler.Sharable
public final class MqttEncoder
extends MessageToMessageEncoder<MqttMessage> {
    public static final MqttEncoder INSTANCE = new MqttEncoder();

    private MqttEncoder() {
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, MqttMessage msg, List<Object> out) throws Exception {
        out.add(MqttEncoder.doEncode(ctx.alloc(), msg));
    }

    static ByteBuf doEncode(ByteBufAllocator byteBufAllocator, MqttMessage message) {
        switch (message.fixedHeader().messageType()) {
            case CONNECT: {
                return MqttEncoder.encodeConnectMessage(byteBufAllocator, (MqttConnectMessage)message);
            }
            case CONNACK: {
                return MqttEncoder.encodeConnAckMessage(byteBufAllocator, (MqttConnAckMessage)message);
            }
            case PUBLISH: {
                return MqttEncoder.encodePublishMessage(byteBufAllocator, (MqttPublishMessage)message);
            }
            case SUBSCRIBE: {
                return MqttEncoder.encodeSubscribeMessage(byteBufAllocator, (MqttSubscribeMessage)message);
            }
            case UNSUBSCRIBE: {
                return MqttEncoder.encodeUnsubscribeMessage(byteBufAllocator, (MqttUnsubscribeMessage)message);
            }
            case SUBACK: {
                return MqttEncoder.encodeSubAckMessage(byteBufAllocator, (MqttSubAckMessage)message);
            }
            case UNSUBACK: 
            case PUBACK: 
            case PUBREC: 
            case PUBREL: 
            case PUBCOMP: {
                return MqttEncoder.encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(byteBufAllocator, message);
            }
            case PINGREQ: 
            case PINGRESP: 
            case DISCONNECT: {
                return MqttEncoder.encodeMessageWithOnlySingleByteFixedHeader(byteBufAllocator, message);
            }
        }
        throw new IllegalArgumentException("Unknown message type: " + message.fixedHeader().messageType().value());
    }

    private static ByteBuf encodeConnectMessage(ByteBufAllocator byteBufAllocator, MqttConnectMessage message) {
        byte[] password;
        byte[] userNameBytes;
        byte[] passwordBytes;
        String userName;
        byte[] willMessageBytes;
        int payloadBufferSize = 0;
        MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        MqttConnectVariableHeader variableHeader = message.variableHeader();
        MqttConnectPayload payload = message.payload();
        MqttVersion mqttVersion = MqttVersion.fromProtocolNameAndLevel(variableHeader.name(), (byte)variableHeader.version());
        if (!variableHeader.hasUserName() && variableHeader.hasPassword()) {
            throw new DecoderException("Without a username, the password MUST be not set");
        }
        String clientIdentifier = payload.clientIdentifier();
        if (!MqttCodecUtil.isValidClientId(mqttVersion, clientIdentifier)) {
            throw new MqttIdentifierRejectedException("invalid clientIdentifier: " + clientIdentifier);
        }
        byte[] clientIdentifierBytes = MqttEncoder.encodeStringUtf8(clientIdentifier);
        payloadBufferSize += 2 + clientIdentifierBytes.length;
        String willTopic = payload.willTopic();
        byte[] willTopicBytes = willTopic != null ? MqttEncoder.encodeStringUtf8(willTopic) : EmptyArrays.EMPTY_BYTES;
        byte[] willMessage = payload.willMessageInBytes();
        byte[] arrby = willMessageBytes = willMessage != null ? willMessage : EmptyArrays.EMPTY_BYTES;
        if (variableHeader.isWillFlag()) {
            payloadBufferSize += 2 + willTopicBytes.length;
            payloadBufferSize += 2 + willMessageBytes.length;
        }
        byte[] arrby2 = userNameBytes = (userName = payload.userName()) != null ? MqttEncoder.encodeStringUtf8(userName) : EmptyArrays.EMPTY_BYTES;
        if (variableHeader.hasUserName()) {
            payloadBufferSize += 2 + userNameBytes.length;
        }
        byte[] arrby3 = passwordBytes = (password = payload.passwordInBytes()) != null ? password : EmptyArrays.EMPTY_BYTES;
        if (variableHeader.hasPassword()) {
            payloadBufferSize += 2 + passwordBytes.length;
        }
        byte[] protocolNameBytes = mqttVersion.protocolNameBytes();
        int variableHeaderBufferSize = 2 + protocolNameBytes.length + 4;
        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + MqttEncoder.getVariableLengthInt(variablePartSize);
        ByteBuf buf = byteBufAllocator.buffer(fixedHeaderBufferSize + variablePartSize);
        buf.writeByte(MqttEncoder.getFixedHeaderByte1(mqttFixedHeader));
        MqttEncoder.writeVariableLengthInt(buf, variablePartSize);
        buf.writeShort(protocolNameBytes.length);
        buf.writeBytes(protocolNameBytes);
        buf.writeByte(variableHeader.version());
        buf.writeByte(MqttEncoder.getConnVariableHeaderFlag(variableHeader));
        buf.writeShort(variableHeader.keepAliveTimeSeconds());
        buf.writeShort(clientIdentifierBytes.length);
        buf.writeBytes(clientIdentifierBytes, 0, clientIdentifierBytes.length);
        if (variableHeader.isWillFlag()) {
            buf.writeShort(willTopicBytes.length);
            buf.writeBytes(willTopicBytes, 0, willTopicBytes.length);
            buf.writeShort(willMessageBytes.length);
            buf.writeBytes(willMessageBytes, 0, willMessageBytes.length);
        }
        if (variableHeader.hasUserName()) {
            buf.writeShort(userNameBytes.length);
            buf.writeBytes(userNameBytes, 0, userNameBytes.length);
        }
        if (variableHeader.hasPassword()) {
            buf.writeShort(passwordBytes.length);
            buf.writeBytes(passwordBytes, 0, passwordBytes.length);
        }
        return buf;
    }

    private static int getConnVariableHeaderFlag(MqttConnectVariableHeader variableHeader) {
        int flagByte = 0;
        if (variableHeader.hasUserName()) {
            flagByte |= 128;
        }
        if (variableHeader.hasPassword()) {
            flagByte |= 64;
        }
        if (variableHeader.isWillRetain()) {
            flagByte |= 32;
        }
        flagByte |= (variableHeader.willQos() & 3) << 3;
        if (variableHeader.isWillFlag()) {
            flagByte |= 4;
        }
        if (variableHeader.isCleanSession()) {
            flagByte |= 2;
        }
        return flagByte;
    }

    private static ByteBuf encodeConnAckMessage(ByteBufAllocator byteBufAllocator, MqttConnAckMessage message) {
        ByteBuf buf = byteBufAllocator.buffer(4);
        buf.writeByte(MqttEncoder.getFixedHeaderByte1(message.fixedHeader()));
        buf.writeByte(2);
        buf.writeByte(message.variableHeader().isSessionPresent() ? 1 : 0);
        buf.writeByte(message.variableHeader().connectReturnCode().byteValue());
        return buf;
    }

    private static ByteBuf encodeSubscribeMessage(ByteBufAllocator byteBufAllocator, MqttSubscribeMessage message) {
        int variableHeaderBufferSize = 2;
        int payloadBufferSize = 0;
        MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        MqttMessageIdVariableHeader variableHeader = message.variableHeader();
        MqttSubscribePayload payload = message.payload();
        for (MqttTopicSubscription topic : payload.topicSubscriptions()) {
            String topicName = topic.topicName();
            byte[] topicNameBytes = MqttEncoder.encodeStringUtf8(topicName);
            payloadBufferSize += 2 + topicNameBytes.length;
            ++payloadBufferSize;
        }
        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + MqttEncoder.getVariableLengthInt(variablePartSize);
        ByteBuf buf = byteBufAllocator.buffer(fixedHeaderBufferSize + variablePartSize);
        buf.writeByte(MqttEncoder.getFixedHeaderByte1(mqttFixedHeader));
        MqttEncoder.writeVariableLengthInt(buf, variablePartSize);
        int messageId = variableHeader.messageId();
        buf.writeShort(messageId);
        for (MqttTopicSubscription topic : payload.topicSubscriptions()) {
            String topicName = topic.topicName();
            byte[] topicNameBytes = MqttEncoder.encodeStringUtf8(topicName);
            buf.writeShort(topicNameBytes.length);
            buf.writeBytes(topicNameBytes, 0, topicNameBytes.length);
            buf.writeByte(topic.qualityOfService().value());
        }
        return buf;
    }

    private static ByteBuf encodeUnsubscribeMessage(ByteBufAllocator byteBufAllocator, MqttUnsubscribeMessage message) {
        int variableHeaderBufferSize = 2;
        int payloadBufferSize = 0;
        MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        MqttMessageIdVariableHeader variableHeader = message.variableHeader();
        MqttUnsubscribePayload payload = message.payload();
        for (String topicName : payload.topics()) {
            byte[] topicNameBytes = MqttEncoder.encodeStringUtf8(topicName);
            payloadBufferSize += 2 + topicNameBytes.length;
        }
        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + MqttEncoder.getVariableLengthInt(variablePartSize);
        ByteBuf buf = byteBufAllocator.buffer(fixedHeaderBufferSize + variablePartSize);
        buf.writeByte(MqttEncoder.getFixedHeaderByte1(mqttFixedHeader));
        MqttEncoder.writeVariableLengthInt(buf, variablePartSize);
        int messageId = variableHeader.messageId();
        buf.writeShort(messageId);
        for (String topicName : payload.topics()) {
            byte[] topicNameBytes = MqttEncoder.encodeStringUtf8(topicName);
            buf.writeShort(topicNameBytes.length);
            buf.writeBytes(topicNameBytes, 0, topicNameBytes.length);
        }
        return buf;
    }

    private static ByteBuf encodeSubAckMessage(ByteBufAllocator byteBufAllocator, MqttSubAckMessage message) {
        int variableHeaderBufferSize = 2;
        int payloadBufferSize = message.payload().grantedQoSLevels().size();
        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + MqttEncoder.getVariableLengthInt(variablePartSize);
        ByteBuf buf = byteBufAllocator.buffer(fixedHeaderBufferSize + variablePartSize);
        buf.writeByte(MqttEncoder.getFixedHeaderByte1(message.fixedHeader()));
        MqttEncoder.writeVariableLengthInt(buf, variablePartSize);
        buf.writeShort(message.variableHeader().messageId());
        Iterator<Integer> iterator = message.payload().grantedQoSLevels().iterator();
        while (iterator.hasNext()) {
            int qos = iterator.next();
            buf.writeByte(qos);
        }
        return buf;
    }

    private static ByteBuf encodePublishMessage(ByteBufAllocator byteBufAllocator, MqttPublishMessage message) {
        MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        MqttPublishVariableHeader variableHeader = message.variableHeader();
        ByteBuf payload = message.payload().duplicate();
        String topicName = variableHeader.topicName();
        byte[] topicNameBytes = MqttEncoder.encodeStringUtf8(topicName);
        int variableHeaderBufferSize = 2 + topicNameBytes.length + (mqttFixedHeader.qosLevel().value() > 0 ? 2 : 0);
        int payloadBufferSize = payload.readableBytes();
        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + MqttEncoder.getVariableLengthInt(variablePartSize);
        ByteBuf buf = byteBufAllocator.buffer(fixedHeaderBufferSize + variablePartSize);
        buf.writeByte(MqttEncoder.getFixedHeaderByte1(mqttFixedHeader));
        MqttEncoder.writeVariableLengthInt(buf, variablePartSize);
        buf.writeShort(topicNameBytes.length);
        buf.writeBytes(topicNameBytes);
        if (mqttFixedHeader.qosLevel().value() > 0) {
            buf.writeShort(variableHeader.messageId());
        }
        buf.writeBytes(payload);
        return buf;
    }

    private static ByteBuf encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(ByteBufAllocator byteBufAllocator, MqttMessage message) {
        MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader)message.variableHeader();
        int msgId = variableHeader.messageId();
        int variableHeaderBufferSize = 2;
        int fixedHeaderBufferSize = 1 + MqttEncoder.getVariableLengthInt(variableHeaderBufferSize);
        ByteBuf buf = byteBufAllocator.buffer(fixedHeaderBufferSize + variableHeaderBufferSize);
        buf.writeByte(MqttEncoder.getFixedHeaderByte1(mqttFixedHeader));
        MqttEncoder.writeVariableLengthInt(buf, variableHeaderBufferSize);
        buf.writeShort(msgId);
        return buf;
    }

    private static ByteBuf encodeMessageWithOnlySingleByteFixedHeader(ByteBufAllocator byteBufAllocator, MqttMessage message) {
        MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        ByteBuf buf = byteBufAllocator.buffer(2);
        buf.writeByte(MqttEncoder.getFixedHeaderByte1(mqttFixedHeader));
        buf.writeByte(0);
        return buf;
    }

    private static int getFixedHeaderByte1(MqttFixedHeader header) {
        int ret = 0;
        ret |= header.messageType().value() << 4;
        if (header.isDup()) {
            ret |= 8;
        }
        ret |= header.qosLevel().value() << 1;
        if (header.isRetain()) {
            ret |= 1;
        }
        return ret;
    }

    private static void writeVariableLengthInt(ByteBuf buf, int num) {
        do {
            int digit = num % 128;
            if ((num /= 128) > 0) {
                digit |= 128;
            }
            buf.writeByte(digit);
        } while (num > 0);
    }

    private static int getVariableLengthInt(int num) {
        int count = 0;
        do {
            ++count;
        } while ((num /= 128) > 0);
        return count;
    }

    private static byte[] encodeStringUtf8(String s) {
        return s.getBytes(CharsetUtil.UTF_8);
    }

}

