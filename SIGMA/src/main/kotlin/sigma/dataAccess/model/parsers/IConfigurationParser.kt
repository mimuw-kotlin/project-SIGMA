package sigma.dataAccess.model.parsers

import sigma.dataAccess.impl.data.Configuration

interface IConfigurationParser {
    fun read(path: String) : Configuration
    fun write(path: String, configuration: Configuration) : Unit
}