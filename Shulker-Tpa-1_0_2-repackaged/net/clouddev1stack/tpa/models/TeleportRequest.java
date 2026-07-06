package net.clouddev1stack.tpa.models;

import java.util.UUID;

public class TeleportRequest {
   private final UUID sender;
   private final UUID target;
   private final RequestType type;
   private final long created;
   private final long expires;

   public TeleportRequest(UUID sender, UUID target, RequestType type, long durationMillis) {
      this.sender = sender;
      this.target = target;
      this.type = type;
      this.created = System.currentTimeMillis();
      this.expires = this.created + durationMillis;
   }

   public UUID getSender() {
      return this.sender;
   }

   public UUID getTarget() {
      return this.target;
   }

   public RequestType getType() {
      return this.type;
   }

   public boolean expired() {
      return System.currentTimeMillis() > this.expires;
   }

   public long secondsLeft() {
      return Math.max(0L, (this.expires - System.currentTimeMillis() + 999L) / 1000L);
   }
}
