---
title: bbac genap kali‼️

---

## bbac genap kali‼️

### Deskripsi

Buatlah Finite State Automaton yang dapat menerima setiap string $S$ dengan kemunculan substring `bbac` sebanyak genap kali. **Perhatikan bahwa tidak ada kemunculan sama sekali termasuk dalam kemunculan genap kali**.

### Batasan

- $|S| \leq 1000$
- $S$ hanya terdiri dari huruf abjad kecil dari `a` - `c`

### Subtasks

1. (50 poin) $|S| \leq 10$
2. (50 poin) Tidak ada batasan tambahan

### Masukan

<pre>
S
</pre>

### Keluaran

Jika FSA menghasilkan **accepts**, maka output tertulis `true`. Sebaliknya, jika FSA menghasilkan **rejects**, maka output tertulis `false`.

### Contoh Masukan 1

```
bbacbbac
```

### Contoh Keluaran 1

```
true
```

### Penjelasan Contoh 1

Perhatikan bahwa terdapat kemunculan substring `bbac` sebanyak $2$ kali, yaitu interval $[1, 4]$ dan $[5, 8]$.

### Contoh Masukan 2

```
bbccbaac
```

### Contoh Keluaran 2

```
false
```