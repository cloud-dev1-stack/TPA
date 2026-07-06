# Shulker-Tpa GUI config
# Actions: CONFIRM, ACCEPT, DENY, CLOSE, COMMAND:/command, CONSOLE:/command
# Supports &, &#RRGGBB, <#RRGGBB> colors and placeholders: %player%, %target%, %sender%, %receiver%, %type%, %time_left%, %warmup%, %prefix%

gui-sounds:
  enabled: true
  confirm: UI_BUTTON_CLICK
  close: UI_BUTTON_CLICK
  accept: ENTITY_EXPERIENCE_ORB_PICKUP
  deny: BLOCK_NOTE_BLOCK_BASS

confirm-tpa:
  title: '&8Send TPA to %target%?'
  size: 27
  fill:
    enabled: true
    material: BLACK_STAINED_GLASS_PANE
    name: ' '
    lore: []
  items:
    confirm:
      slot: 11
      material: LIME_CONCRETE
      amount: 1
      custom-model-data: 0
      glow: true
      name: '&a&lSend Request'
      lore:
        - '&7Teleport to &f%target%&7.'
        - '&eClick to confirm.'
      action: CONFIRM
    cancel:
      slot: 15
      material: RED_CONCRETE
      amount: 1
      glow: false
      name: '&c&lCancel'
      lore:
        - '&7Close this menu.'
      action: CLOSE

confirm-tpahere:
  title: '&8Ask %target% to come?'
  size: 27
  fill:
    enabled: true
    material: BLACK_STAINED_GLASS_PANE
    name: ' '
    lore: []
  items:
    confirm:
      slot: 11
      material: ENDER_PEARL
      amount: 1
      glow: true
      name: '&a&lSend TPAHere'
      lore:
        - '&7Ask &f%target% &7to teleport to you.'
        - '&eClick to confirm.'
      action: CONFIRM
    cancel:
      slot: 15
      material: BARRIER
      amount: 1
      glow: false
      name: '&c&lCancel'
      lore:
        - '&7Close this menu.'
      action: CLOSE

accept-menu:
  title: '&8Accept Teleport Request'
  size: 54
  fill:
    enabled: true
    material: GRAY_STAINED_GLASS_PANE
    name: ' '
    lore: []
  request-item:
    # You can replace slots with any list of slots you want.
    slots: [10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34]
    material: PLAYER_HEAD
    amount: 1
    glow: true
    custom-model-data: 0
    name: '&aAccept &f%sender%'
    lore:
      - '&7Type: &f%type%'
      - '&7Expires in: &f%time_left%s'
      - ''
      - '&aLeft Click: Accept'
      - '&cRight Click: Deny'
    left-click-action: ACCEPT
    right-click-action: DENY
    shift-click-action: ACCEPT
    middle-click-action: DENY
  items:
    close:
      slot: 49
      material: BARRIER
      name: '&cClose'
      lore:
        - '&7Close this menu.'
      glow: false
      action: CLOSE

deny-menu:
  title: '&8Deny Teleport Request'
  size: 54
  fill:
    enabled: true
    material: RED_STAINED_GLASS_PANE
    name: ' '
    lore: []
  request-item:
    slots: [10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34]
    material: PLAYER_HEAD
    amount: 1
    glow: false
    name: '&cDeny &f%sender%'
    lore:
      - '&7Type: &f%type%'
      - '&7Expires in: &f%time_left%s'
      - ''
      - '&cLeft Click: Deny'
      - '&aRight Click: Accept'
    left-click-action: DENY
    right-click-action: ACCEPT
    shift-click-action: DENY
    middle-click-action: ACCEPT
  items:
    close:
      slot: 49
      material: BARRIER
      name: '&cClose'
      lore:
        - '&7Close this menu.'
      action: CLOSE

list-menu:
  title: '&8Pending Teleport Requests'
  size: 54
  fill:
    enabled: true
    material: BLACK_STAINED_GLASS_PANE
    name: ' '
    lore: []
  request-item:
    slots: [10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34]
    material: PLAYER_HEAD
    amount: 1
    glow: false
    name: '&d%sender%'
    lore:
      - '&7Type: &f%type%'
      - '&7Expires in: &f%time_left%s'
      - ''
      - '&aLeft Click: Accept'
      - '&cRight Click: Deny'
    left-click-action: ACCEPT
    right-click-action: DENY
    shift-click-action: ACCEPT
    middle-click-action: DENY
  items:
    close:
      slot: 49
      material: BARRIER
      name: '&cClose'
      lore:
        - '&7Close this menu.'
      action: CLOSE
