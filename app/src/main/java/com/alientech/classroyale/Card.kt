package com.alientech.classroyale

class Card(
    name: String,
    HP: Int,
    attackDamage: Int,
    description: String,
    rarity: String,
    isPersonCard: Boolean,
    isDisplayingProperties: Boolean,
    level: Int,
    XP: Int,
    XPToLevelUp: Int
) {
    var name: String
        get() {return this.name}
        set(value) {this.name = name}

    var HP: Int
        get() {return this.HP}
        set(value) {
            this.HP = HP
            for (i in 0 until this.level - 1) {
                upgradeHP()
            }
        }

    var attackDamage: Int
        get() {return this.attackDamage}
        set(value) {
            this.attackDamage = attackDamage
            for (i in 0 until this.level - 1) {
                upgradeAttackDamage()
            }
        }

    var description: String
        get() {return this.description}
        set(value) {this.description = description}

    var rarity: String
        get() {return this.rarity}
        set(value) {this.rarity = rarity}

    var isPersonCard: Boolean
        get() {return this.isPersonCard}
        set(value) {this.isPersonCard = isPersonCard}

    var isDisplayingProperties: Boolean
        get() {return this.isDisplayingProperties}
        set(value) {this.isDisplayingProperties = isDisplayingProperties}

    var level: Int
        get() {return this.level}
        set(value) {this.level = level}

    var xp: Int
        get() {return xp}
        set(value) {this.xp = xp}

    var xpToLevelUp: Int
        get() {return xpToLevelUp}
        set(value) {
            this.xpToLevelUp = xpToLevelUp
            for (i in 0 until level - 1) {
                upgradeXPToLevelUp()
            }
        }

    fun upgradeXPToLevelUp() {
        this.xpToLevelUp += (0.3 * this.xpToLevelUp).toInt()
    }

    fun upgradeHP() {
        this.HP += (0.15 * this.HP).toInt()
    }

    fun upgradeAttackDamage() {
        this.attackDamage += (0.15 * this.attackDamage).toInt()
    }

    fun study() {
        if (this.level < 12) {
            if (this.xp >= this.xpToLevelUp) {
                upgradeHP()
                upgradeAttackDamage()
                this.level = this.level + 1
                this.xp = this.xp - this.xpToLevelUp
                upgradeXPToLevelUp()
            }
        }
    }

    fun play() {
        val performance = this.evaluatePerformance()
        this.xp = this.xp + performance
    }

    fun evaluatePerformance(): Int {
        return 34433434;
    }

    companion object {
        const val COMMON = "Common"
        const val UNCOMMON = "Uncommon"
        const val RARE = "Rare"
        const val ULTRA_RARE = "Ultra Rare"
        const val LEGENDARY = "Legendary"
        const val MYTHICAL = "Mythical"
    }
}