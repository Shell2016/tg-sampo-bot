package ru.michaelshell.sampo_bot.mapper;

public interface Mapper<F, T> {

    T map(F fromDto);

}
